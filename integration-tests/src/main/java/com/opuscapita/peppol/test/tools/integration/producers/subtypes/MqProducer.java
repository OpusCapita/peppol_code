package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.container.process.route.Route;
import com.opuscapita.peppol.commons.mq.ConnectionString;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.test.tools.integration.producers.Producer;
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
        File directory;
        try {
            directory = new File(sourceDirectory);
            if (!directory.isDirectory()) {
                logger.error(this.sourceDirectory + " doesn't exist!");
                return;
            }
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
            for (File file : directory.listFiles()) {
                if (file.isFile()) {
                    ContainerMessage cm = createContainerMessageFromFile(file);
                    logger.info("MqProducer: Sending message via MessageQueue to " + destinationQueue + " -> " + endpoint);
                    mq.convertAndSend(destinationQueue + ConnectionString.QUEUE_SEPARATOR + "", cm);
                    //channel.basicPublish("", destinationQueue, null, cm);
                    logger.info("MqProducer: published to MQ: " + cm.getFileName());
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
        ContainerMessage cm = new ContainerMessage("integration-tests", file.getAbsolutePath(),
                new Endpoint("integration-tests", ProcessType.TEST))
                .setDocumentInfo(documentLoader.load(file, new Endpoint("outbound", ProcessType.TEST)));
        cm.setStatus(new Endpoint("integration-tests", ProcessType.TEST), file.getName());
        List<String> endpoints = Collections.singletonList(endpoint); //new queue for integration tests
        Route route = new Route();
        route.setEndpoints(endpoints);
        cm.getProcessingInfo().setRoute(route);
        return cm;
    }
}
