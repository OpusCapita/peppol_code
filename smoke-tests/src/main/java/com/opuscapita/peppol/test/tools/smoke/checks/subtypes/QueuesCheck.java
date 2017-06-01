package com.opuscapita.peppol.test.tools.smoke.checks.subtypes;

import com.opuscapita.peppol.test.tools.smoke.checks.Check;
import com.opuscapita.peppol.test.tools.smoke.checks.CheckResult;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by bambr on 16.20.10.
 */
public class QueuesCheck extends Check {

    private final static Logger logger = LogManager.getLogger(QueuesCheck.class);
    final String _mesage = "Smoke Test MQ message";

    public QueuesCheck(String moduleName, Map<String, Object> params) {
        super(moduleName, params);
    }

    @Override
    public CheckResult run() {
        Connection connection = null;
        Channel channel = null;
        CheckResult checkResult = null;
        String[] queues = ((String)rawConfig.get("queue")).trim().split(" ");
        try{
            logger.info("QueuesCheck: Start");
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost((String)rawConfig.get("host"));
            factory.setPort((int)(Object)rawConfig.get("port"));
            factory.setUsername((String)rawConfig.get("username"));
            factory.setPassword((String)rawConfig.get("password"));
            factory.setConnectionTimeout(500);
            connection = factory.newConnection();
            channel = connection.createChannel();
            boolean success = true;
            for(String queue : queues) {
                try {
                    channel.queueDeclarePassive(queue);
                }
                catch (Exception ex){
                    logger.warn("Error: Queue " + queue +" not found!");
                    success = false;
                }
            }
            if(!success)
                throw new Exception("Queue not found!");
            checkResult = new CheckResult(moduleName, true, "Queues check succeeded ", rawConfig);
        } catch (Exception ex){
            ex.printStackTrace();
            checkResult =  new CheckResult(moduleName, false, "Queues check failed " + ex, rawConfig);
        }
        finally {
            try{
                if (channel != null)
                    channel.close();
                if(connection != null)
                    connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return checkResult;
    }
}
