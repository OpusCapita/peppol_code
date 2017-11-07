package com.opuscapita.peppol.test.tools.integration.subscribers.subtypes;

import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;

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


    public DbSubscriber(Object timeout, String dbConnection, Object query) {
        super(timeout);
        this.dbConnection = dbConnection;
        this.query = (String) query;
    }

    @Override
    protected void fetchConsumable() {
        ResultSet resultSet;
        try {
            Properties props = new Properties();
            props.put("useJDBCCompliantTimezoneShift", "true");
            props.put("serverTimezone", "UTC");
            java.sql.Connection conn = DriverManager.getConnection(dbConnection, props);
            PreparedStatement statement = conn.prepareStatement(query);
            resultSet = statement.executeQuery();
            resultSet.next();
            consumable = resultSet.getInt(1) == 1 ? 1 : null;
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }
}
