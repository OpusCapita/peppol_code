package com.opuscapita.peppol.test.tools.smoke.checks.subtypes;

import com.opuscapita.peppol.test.tools.smoke.checks.Check;
import com.opuscapita.peppol.test.tools.smoke.checks.CheckResult;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Map;

/**
 * Created by bambr on 16.20.10.
 */
public class MqConnectionCheck extends Check {

    private final static String QUEUE_NAME = "persistence-test";

    public MqConnectionCheck(String moduleName, Map<String, String> params) {
        super(moduleName, params);
    }

    @Override
    public CheckResult run() {
        Connection connection = null;
        Channel channel = null;
        CheckResult checkResult = null;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(rawConfig.get("host"));
            factory.setPort((int)(Object)rawConfig.get("port"));
            factory.setUsername(rawConfig.get("username"));
            factory.setPassword(rawConfig.get("password"));
            factory.setConnectionTimeout(500);
            connection = factory.newConnection();
            checkResult = new CheckResult(name, true, "MQ Connection check succeeded ", rawConfig);

        } catch (Exception ex){
            ex.printStackTrace();
            checkResult =  new CheckResult(name, false, "MQ Connection check failed " + ex, rawConfig);
        }
        finally {
            try {
                if (channel != null)
                    channel.close();
                if (connection != null)
                    connection.close();
            } catch (Exception inore){}
        }
        return checkResult;
    }

}
