package com.opuscapita.peppol.test.tools.integration.subscribers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;
import com.opuscapita.peppol.test.tools.integration.util.StorageService;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class DbSubscriber extends Subscriber {
    private final String dbConnection;
    private final String query;
    @Autowired
    StorageService storageService;

    public DbSubscriber(Object timeout, String dbConnection, Object query) {
        super(timeout);
        this.dbConnection = dbConnection;
        this.query = (String) query;
    }

    @Override
    public void run() {
        //next() getInt(1)
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
            for (Consumer consumer : consumers) {
                consumer.consume(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
