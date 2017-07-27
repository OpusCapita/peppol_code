package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.container.process.route.Route;
import com.opuscapita.peppol.commons.mq.ConnectionString;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import com.opuscapita.peppol.test.tools.integration.util.EvilContainerMessage;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by gamanse1 on 2016.11.14..
 */
@SuppressWarnings("Duplicates")
@Component
@Scope("prototype")
public class MqProducer implements Producer {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(MqProducer.class);
    private String endpoint;
    private MessageQueue mq;
    private String dbConnection = null;
    private String dbPreprocessQuery = null;
    private Map<String, String> mqSettings;
    private String sourceDirectory;
    private String destinationQueue;
    private ProcessType processType = ProcessType.TEST;  //default
    private String endpointSourceName = "integration-tests"; //default

    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    @Autowired
    private DocumentLoader documentLoader;

    public MqProducer() {
    }

    public MqProducer(Map<String, String> mqSettings, String sourceDirectory, String destinationQueue, String endpoint, String dbConnection, String dbPreprocessQuery, MessageQueue mq) {
        this.mqSettings = mqSettings;
        this.sourceDirectory = sourceDirectory;
        this.destinationQueue = destinationQueue;
        this.endpoint = endpoint;
        this.dbConnection = dbConnection;
        this.dbPreprocessQuery = dbPreprocessQuery;
        this.mq = mq;
    }

    /*
    * Check if files exist in directory-> exit if no
    * Check if DB preprocess specified -> Try to connect and run preprocess querry -> exit if fails
    * Check if able to connect to MQ -> exit if no
    * */
    @SuppressWarnings({"ConstantConditions", "finally"})
    @Override
    public void run() {
        Connection connection = null;
        Channel channel = null;
        File folder;
        try {
            folder = new File(sourceDirectory);
        } catch (Exception ex) {
            logger.error("Error reading: " + sourceDirectory, ex);
            return;
        }

        try {
            if (dbPreprocessNeeded())
                executeDbPreprocess();
        } catch (Exception ex1) {
            logger.info("Error executing DB preprocess `!", ex1);
            return;
        }

        try {
            if(folder.isFile() && folder.exists()) {
                processSingleFile(folder);
            }
            else {
                for (File file : folder.listFiles()) {
                    if (file.isFile())
                        processSingleFile(file);
                }
            }
        } catch (Exception ex2) {
            logger.error("Error running MqProducer!", ex2);
        } finally {
            try {
                if (connection != null)
                    connection.close();
                if (channel != null)
                    channel.close();
            } catch (Exception ignore) {
            }
        }
    }

    private void processSingleFile(File file) throws Exception {
        ContainerMessage cm = createContainerMessageFromFile(file);
        logger.info("MqProducer: Sending message via MessageQueue to " + destinationQueue + " -> " + endpoint);
        mq.convertAndSend(destinationQueue + ConnectionString.QUEUE_SEPARATOR + "", cm);
        logger.info("MqProducer: published to MQ: " + cm.getFileName());
    }

    private void executeDbPreprocess() {
        logger.info("Starting DB preprocess query execution!");
        Properties props = new Properties();
        props.put("useJDBCCompliantTimezoneShift", "true");
        props.put("serverTimezone", "UTC");
        try {
            java.sql.Connection conn = DriverManager.getConnection(dbConnection, props);
            logger.info("MqProducer: db connection established!");
            PreparedStatement statement = conn.prepareStatement(dbPreprocessQuery);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("MqProducer: unable to run DB preprocess query: " + e);
        }
    }

    private boolean dbPreprocessNeeded() {
        return (dbConnection != null && !dbConnection.isEmpty() && dbPreprocessQuery != null && !dbPreprocessQuery.isEmpty());
    }

    @SuppressWarnings("ConstantConditions")
    private ContainerMessage createContainerMessageFromFile(File file) throws Exception {
        return (file.getName().contains("invalid")) ? createInvalidContainerMessage(file) : createValidContainerMessage(file);
    }

    private ContainerMessage createValidContainerMessage(File file) throws Exception {
        Endpoint source = new Endpoint(endpointSourceName, ProcessType.TEST);
        ContainerMessage cm =  new ContainerMessage("integration-tests", file.getAbsolutePath(),source)
                .setDocumentInfo(documentLoader.load(file, new Endpoint("integration-tests", ProcessType.TEST)));
        //final endpoint
        cm.setStatus(new Endpoint("integration-tests", processType), file.getName());
        List<String> endpoints = Collections.singletonList(endpoint); //new queue for integration tests
        cm.getProcessingInfo().setTransactionId("transactionId");
        Route route = new Route();
        route.setEndpoints(endpoints);
        cm.getProcessingInfo().setRoute(route);
        return cm;
    }

    private ContainerMessage createInvalidContainerMessage(File file) throws Exception {
        Endpoint source = new Endpoint(endpointSourceName, ProcessType.TEST);
        ContainerMessage cm =  new EvilContainerMessage("integration-tests", file.getAbsolutePath(),source)
                .setDocumentInfo(documentLoader.load(file, new Endpoint("integration-tests", ProcessType.TEST)));

        return cm;
    }

    public void setProcessType(String type){
        switch (type){
            case "outbound":
                processType = ProcessType.OUT_OUTBOUND;
                return;
            case "test":
                processType = ProcessType.TEST;
                return;
            default:
                throw new IllegalArgumentException("process type not recognized!");
        }
    }

    public void setEndpointSourceName(String endpointSourceName) {
        this.endpointSourceName = endpointSourceName;
    }
}
