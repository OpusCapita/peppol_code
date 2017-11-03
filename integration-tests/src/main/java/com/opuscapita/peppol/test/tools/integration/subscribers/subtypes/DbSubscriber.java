package com.opuscapita.peppol.test.tools.integration.subscribers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class DbSubscriber extends Subscriber {
    private final static Logger logger = LoggerFactory.getLogger(DbSubscriber.class);
    private final String dbConnection;
    private final String query;


    public DbSubscriber(Object timeout, String dbConnection, Object query) {
        super(timeout);
        this.dbConnection = dbConnection;
        this.query = (String) query;
    }

    @Override
    public List<TestResult> run() {
        logger.info("DbSubscriber: started!");

        try {
            for (int i =0; i < 30; i ++ ) {
                int result = getQuerryResult();
                if (result < 1) {
                    logger.info("DbSubscriber: got no result, retrying in " + timeout);
                    Thread.sleep(timeout);
                } else {
                    for (Consumer consumer : consumers) {
                        TestResult testResult = consumer.consume(result);
                        testResults.add(testResult);
                        return testResults;
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return testResults;
    }

    private int getQuerryResult() {
        ResultSet resultSet;

        try {
            Properties props = new Properties();
            props.put("useJDBCCompliantTimezoneShift", "true");
            props.put("serverTimezone", "UTC");
            java.sql.Connection conn = null;

            conn = DriverManager.getConnection(dbConnection, props);
            PreparedStatement statement = conn.prepareStatement(query);
            resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e1) {
            e1.printStackTrace();
            return  -1;
        }
    }
}
