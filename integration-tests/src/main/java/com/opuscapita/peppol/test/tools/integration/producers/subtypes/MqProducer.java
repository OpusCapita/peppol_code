package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.container.route.Route;
import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.LogManager;

import java.io.File;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.*;

/**
 * Created by gamanse1 on 2016.11.14..
 */
@SuppressWarnings("Duplicates")
public class MqProducer implements Producer {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(MqProducer.class);
    private final String dbConnection;
    private final String dbPreprocessQuery;
    private Map<String, String> mqSettings;
    private String sourceDirectory;
    private String destinationQueue;
    private final String QUEUE_NAME = "integration-validation-test";
    DocumentLoader documentLoader = new DocumentLoader();

    public MqProducer(Map<String, String> mqSettings, String sourceDirectory, String destinationQueue, String dbConnection, String dbPreprocessQuery) {
        this.mqSettings = mqSettings;
        this.sourceDirectory = sourceDirectory;
        this.destinationQueue = destinationQueue;
        this.dbConnection = dbConnection;
        this.dbPreprocessQuery = dbPreprocessQuery;
    }

    /*
    * Check if files exist in directory-> exit if no
    * Check if DB preprocess specified -> Try to connect and run preprocess querry -> exit if fails
    * Check if able to connect to MQ -> exit if no
    * */
    @Override
    public void run() {
        Connection connection = null;
        Channel channel = null;
        File directory = null;
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
            if (dbPreprocessNeeded()) {
                logger.info("Starting DB preprocess query execution!");
                Properties props = new Properties();
                props.put("useJDBCCompliantTimezoneShift", "true");
                props.put("serverTimezone", "UTC");
                java.sql.Connection conn = DriverManager.getConnection(dbConnection, props);
                PreparedStatement statement = conn.prepareStatement(dbPreprocessQuery);
                statement.executeUpdate();
            }
        } catch (Exception ex1) {
            logger.info("Error executing DB preprocess query!", ex1);
            return;
        }

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(mqSettings.get("host"));
            factory.setPort((int) (Object) mqSettings.get("port"));
            factory.setUsername(mqSettings.get("username"));
            factory.setPassword(mqSettings.get("password"));
            factory.setConnectionTimeout(500);
            connection = factory.newConnection();
            channel = connection.createChannel();
            logger.info("Created channel for MQ!");
            channel.queueDeclare(destinationQueue, true, false, false, null); //validator queue
            channel.queueDeclare(QUEUE_NAME, false, false, true, null);       //integration-tests queue
            for (File file : directory.listFiles()) {
                if (file.isFile()) {
                    ContainerMessage cm = new ContainerMessage(file.getName(), file.getName(), new Endpoint("test", Endpoint.Type.PEPPOL))
                            .setBaseDocument(documentLoader.load(file));
                    Route route = new Route();
                    List<String> endpoints = Arrays.asList(QUEUE_NAME); //new queue for integration tests
                    route.setEndpoints(endpoints);
                    cm.setRoute(route);
                    channel.basicPublish("", destinationQueue, null, cm.convertToJsonByteArray());
                    logger.info("MqProducer: published to MQ: "+cm.getFileName());
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
            } catch (Exception inore) {
            }
            return;
        }
    }

    private boolean dbPreprocessNeeded() {
        return (dbConnection != null && !dbConnection.isEmpty() && dbPreprocessQuery != null && !dbPreprocessQuery.isEmpty());
    }

    private ContainerMessage prepareMessage(String fileName, String metadata) {
       return new ContainerMessage(metadata, fileName, new Endpoint("test", Endpoint.Type.PEPPOL));
    }

}
