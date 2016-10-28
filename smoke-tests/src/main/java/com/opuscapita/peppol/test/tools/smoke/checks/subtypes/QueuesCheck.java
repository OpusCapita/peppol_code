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
public class QueuesCheck extends Check {

    final String _mesage = "Smoke Test MQ message";

    public QueuesCheck(String moduleName, Map<String, String> params) {
        super(moduleName, params);
    }

    @Override
    public CheckResult run() {
        Connection connection = null;
        Channel channel = null;
        CheckResult checkResult = null;
        String[] queues = rawConfig.get("queue").trim().split(" ");
        try{
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setPort(15672);
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setConnectionTimeout(500);
            connection = factory.newConnection();
            channel = connection.createChannel();

            for(String queue : queues) {
                channel.queueDeclare(queue, true, false, false, null);
                channel.basicPublish("", queue, null, _mesage.getBytes("UTF-8"));
            }

            channel.close();
            connection.close();
            checkResult = new CheckResult(name, true, "Queues check succeeded ", rawConfig);

        } catch (Exception ex){
            ex.printStackTrace();
            checkResult =  new CheckResult(name, false, "Queues check failed " + ex, rawConfig);
        }
        return checkResult;
    }
}
