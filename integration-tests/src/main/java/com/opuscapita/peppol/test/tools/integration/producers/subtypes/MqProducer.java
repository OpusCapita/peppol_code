package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.mq.ConnectionString;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import com.opuscapita.peppol.test.tools.integration.util.ContainerMessageCreator;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by gamanse1 on 2016.11.14..
 */
@SuppressWarnings("Duplicates")
@Component
@Scope("prototype")
public class MqProducer implements Producer {
    private final static Logger logger = LoggerFactory.getLogger(MqProducer.class);
    private Map<String, String> containerMessageProperties;
    private MessageQueue mq;
    private String dbConnection = null;
    private String dbPreprocessQuery = null;
    private Map<String, String> mqSettings;
    private String destinationQueue;
    private String sourceDirectory;

    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    @Autowired
    private DocumentLoader documentLoader;

    public MqProducer() {
    }

    public MqProducer(Map<String, String> mqSettings, String destinationQueue, String sourceDirectory, @NotNull Map<String, String> containerMessageProperties, MessageQueue mq, String dbConnection, String dbPreprocessQuery) {
        this.mqSettings = mqSettings;
        this.destinationQueue = destinationQueue;
        this.sourceDirectory = sourceDirectory;
        this.containerMessageProperties = containerMessageProperties;
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

        /* should be moved to separate preprocessor*/
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
        ContainerMessageCreator cmc = new ContainerMessageCreator(documentLoader, containerMessageProperties);
        ContainerMessage cm = cmc.createContainerMessage(file);
        logger.info("MqProducer: Sending message via MessageQueue to " + destinationQueue);
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

}
