package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;
/**
 * Created by gamanse1 on 2016.11.17..
 */
public class DbProducer implements Producer {
    private final static Logger logger = LoggerFactory.getLogger(DbProducer.class);
    private final String connection;
    private String query;

    public DbProducer(String dbConnection, Object query) {
        this.connection = dbConnection;
        this.query = (String) query;
    }

    @Override
    @SuppressWarnings("Duplicates")
    public void run() {
        if(query == null || query.isEmpty()){
            logger.warn("DB producer: empty sql query, exiting!");
            return;
        }
        try {
            logger.info("Starting DB preprocess query execution!");
            Properties props = new Properties();
            props.put("useJDBCCompliantTimezoneShift", "true");
            props.put("serverTimezone", "UTC");
            java.sql.Connection conn = DriverManager.getConnection(connection, props);
            PreparedStatement statement = conn.prepareStatement(query);
            statement.executeUpdate();
        }
        catch (Exception ex){
            ex.printStackTrace();
            logger.error("failed to run DbProducer, ", ex);
        }
    }
}
