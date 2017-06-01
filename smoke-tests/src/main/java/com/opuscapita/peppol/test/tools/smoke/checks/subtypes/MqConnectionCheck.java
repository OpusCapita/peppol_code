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

    public MqConnectionCheck(String moduleName, Map<String, Object> params) {
        super(moduleName, params);
    }

    @Override
    public CheckResult run() {
        Connection connection = null;
        CheckResult checkResult = null;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost((String)rawConfig.get("host"));
            factory.setPort((int)(Object)rawConfig.get("port"));
            factory.setUsername((String)rawConfig.get("username"));
            factory.setPassword((String)rawConfig.get("password"));
            factory.setConnectionTimeout(500);
            connection = factory.newConnection();
            Channel channel = connection.createChannel();
            checkResult = new CheckResult(moduleName, true, "MQ Connection check succeeded ", rawConfig);

        } catch (Exception ex){
            ex.printStackTrace();
            checkResult =  new CheckResult(moduleName, false, "MQ Connection check failed " + ex, rawConfig);
        }
        finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (Exception inore){}
        }
        return checkResult;
    }

}
