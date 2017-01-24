package com.opuscapita.peppol.test.tools.integration.subscribers.subtypes;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.log4j.LogManager;

import java.util.List;
import java.util.Map;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class MqSubscriber extends Subscriber {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(MqSubscriber.class);
    private final String queue;
    private Map<String, String> mqSettings;
    private final Gson gson = ContainerMessage.prepareGson();

    public MqSubscriber(Object timeout, Map<String, String> mqSettings, Object queue) {
        super(timeout);
        this.mqSettings = mqSettings;
        this.queue = (String) queue;
    }

    @Override
    public List<TestResult> run() {
        logger.info("MqSubscriber: started!");
        Connection connection = null;
        //TODO get messages from MessageListener ?????
        Channel channel = null;
      /*  try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(mqSettings.get("host"));
            factory.setPort((int) (Object) mqSettings.get("port"));
            factory.setUsername(mqSettings.get("username"));
            factory.setPassword(mqSettings.get("password"));
            factory.setConnectionTimeout(500);
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            //Rabbit consumer which simply gets messages from the queue
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return testResults;
    }
}
