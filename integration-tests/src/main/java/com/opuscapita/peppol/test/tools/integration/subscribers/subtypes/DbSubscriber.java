package com.opuscapita.peppol.test.tools.integration.subscribers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import org.apache.log4j.LogManager;

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
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(DbSubscriber.class);
    private final String dbConnection;
    private final String query;


    public DbSubscriber(Object timeout, String dbConnection, Object query) {
        super(timeout);
        this.dbConnection = dbConnection;
        this.query = (String) query;
    }

    @Override
    public List<TestResult> run() {
        try {
            logger.info("DbSubscriber: started!");
            String executionResult = getQuerryResult();
            if (executionResult.equals("0")) {
                logger.info("DbSubscriber: got no result, retrying in " + timeout);
                Thread.sleep(timeout);
                executionResult = getQuerryResult(); //second attempt
                if(executionResult.equals("0"))
                    logger.info("DbSubscriber: still no results");
                else
                    logger.info("DbSubscriber: got the result after retry, nice");
            }
            for (Consumer consumer : consumers) {
                TestResult testResult = consumer.consume(executionResult);
                testResults.add(testResult);
            }
        } catch (
                InterruptedException e) {
            e.printStackTrace();
        }
        return testResults;
    }

    private String getQuerryResult() {
        ResultSet resultSet = null;
        try {
            Properties props = new Properties();
            props.put("useJDBCCompliantTimezoneShift", "true");
            props.put("serverTimezone", "UTC");
            java.sql.Connection conn = null;

            conn = DriverManager.getConnection(dbConnection, props);
            PreparedStatement statement = conn.prepareStatement(query);
            resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getString(1);
        } catch (SQLException e1) {
            e1.printStackTrace();
            return  e1.toString();
        }
    }
}
