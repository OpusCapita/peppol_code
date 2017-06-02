package com.opuscapita.peppol.test.tools.smoke.checks.subtypes;

import com.opuscapita.peppol.test.tools.smoke.checks.Check;
import com.opuscapita.peppol.test.tools.smoke.checks.CheckResult;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by bambr on 16.20.10.
 */
public class DbConnectionCheck extends Check {


    public DbConnectionCheck(String moduleName, Map<String, Object> params) {
        super(moduleName, params);
    }

    @Override
    public CheckResult run() {
        CheckResult result;
        try (Connection conn = getConnection()){
            result = new CheckResult(moduleName, true, "the database: " + rawConfig.get("DB-name") +  " is reachable, ", rawConfig);
        } catch (Exception ex) {
            ex.printStackTrace();
            result = new CheckResult(moduleName, false, "the database: " + rawConfig.get("DB-name") + " not reachable, " + ex, rawConfig);
        }
        return result;
    }

    private Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.put("user", rawConfig.get("username"));
        props.put("password", rawConfig.get("password"));
        props.put("useJDBCCompliantTimezoneShift","true");
        props.put("serverTimezone","UTC");
        Connection conn = DriverManager.getConnection(
                "jdbc:" + rawConfig.get("driver") + "://" +
                        rawConfig.get("host") +
                        ":" + String.valueOf(rawConfig.get("port"))+ "/"+
                        rawConfig.get("DB-name"),
                props);
        return conn;
    }
}
