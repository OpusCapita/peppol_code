package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.LogManager;

import java.io.File;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Properties;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public class MqProducer implements Producer {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(Producer.class);
    private final String dbConnection;
    private final String dbPreprocessQuery;
    private Map<String, String> dbPreprocessSettting = null;
    private Map<String, String> mqSettings;
    private String sourceFolder;
    private String destinationQueue;

    public MqProducer(Map<String, String> mqSettings, String sourceFolder, String destinationQueue, String dbConnection, String dbPreprocessQuery) {
        this.mqSettings = mqSettings;
        this.sourceFolder = sourceFolder;
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
        try {
            File directory = new File(sourceFolder);
            if (!directory.isDirectory()) {
                logger.error(this.sourceFolder + " doesn't exist!");
                return;
            }
        } catch (Exception ex) {
            logger.error("Error reading: " + sourceFolder, ex);
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

            //TODO add Mq header and send to destinationQueue
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
        }
    }

    private boolean dbPreprocessNeeded() {
        return (dbConnection != null && !dbConnection.isEmpty() && dbPreprocessQuery != null && !dbPreprocessQuery.isEmpty());
    }

}
