package com.opuscapita.peppol.test.tools.integration;

import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.SncEntity;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.test.tools.integration.configs.IntegrationTestConfig;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import com.opuscapita.peppol.test.tools.integration.util.IntegrationTestConfigReader;
import com.opuscapita.peppol.test.tools.integration.util.IntegrationTestProperties;
import com.opuscapita.peppol.test.tools.integration.util.LoggingResultBuilder;
import com.opuscapita.peppol.test.tools.integration.util.MqListener;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.AbstractRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


/**
 * Created by gamanse1 on 2016.11.14..
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.opuscapita.peppol.commons", "com.opuscapita.peppol.test.tools.integration"})
public class IntegrationTestApp implements RabbitListenerConfigurer {
    private final static Logger logger = LogManager.getLogger(IntegrationTestApp.class);
    static String configFile;
    static String testResultFileName;
    static String templateDir;
    public static String tempDir;
    private static List<MqListener> mqListeners = new ArrayList<>();

    @Autowired
    private Environment environment;

    @Autowired
    private MessageQueue mq;

    private static MessageQueue staticMq;

    @Autowired
    private IntegrationTestProperties props;

    @PostConstruct
    public void init() {
        staticMq = mq;
    }

    public static void main(String[] args) {
        logger.info("IntegrationTestApp : Starting!");
        SpringApplication.run(IntegrationTestApp.class);

        if (args.length < 4 || args[0] == null || args[0].isEmpty()) {
            logger.error("Not all command line arguments specified!");
            logger.error("Required arguments are: configFile, testResultFileName, templateDir, tempDir");
            System.exit(1);
        }
        configFile = args[0];
        testResultFileName = args[1];
        templateDir = args[2];
        tempDir = args[3];

        File tempFolder = new File(tempDir);
        if(!tempFolder.exists() || !tempFolder.isDirectory()) {
            logger.error("unable to find temp directory: " + tempDir + " exiting!");
            return;
        }
        if (new File(configFile).isDirectory())
            configFile = configFile + "\\configuration.yaml";

        IntegrationTestConfig config = new IntegrationTestConfigReader(configFile, staticMq).initConfig();
        List<TestResult> testResults = config.runTests();
        new LoggingResultBuilder().processResult(testResults); //outputs the result to console

        //new HtmlResultBuilder(testResultFileName, templateDir).processResult(testResults);
        //cleaning temp directory
        if(tempDir.startsWith("C")) { //hack to clean windows directory, no need to clean docker directory however
            try {
                FileUtils.cleanDirectory(new File(tempDir));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("IntegrationTestApp : Ended!");
    }

    public static void registerMqListener(MqListener listener){
        mqListeners.add(listener);
    }

    @Bean
    public ServiceNow serviceNowRest() {
        return new ServiceNow() {
            @Override
            public void insert(SncEntity sncEntity) throws IOException {
                System.out.println("Inserted incident: " + sncEntity);
            }
        };
    }

    private void createIntegrationTestQueue() {
        com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
        factory.setHost(props.getRabbitmq().getHost());
        factory.setPort(props.getRabbitmq().getPort());
        factory.setUsername(props.getRabbitmq().getUsername());
        factory.setPassword(props.getRabbitmq().getPassword());
        factory.setConnectionTimeout(500);
        Connection connection = null;
        try {
            connection = factory.newConnection();
            Channel channel = connection.createChannel();
            for (String queue: props.getQueues()) {
                channel.queueDeclare(queue, false, false, false, null);       //integration-tests queue
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    //Rabbit here, configuration comes from application.properties
    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        createIntegrationTestQueue();
        AbstractRabbitListenerEndpoint listenerEndpoint = new AbstractRabbitListenerEndpoint() {
            @Override
            protected MessageListener createMessageListener(MessageListenerContainer container) {
                return new MessageListener() {
                    @Override
                    public void onMessage(Message message) {
                        String consumerQueue = message.getMessageProperties().getConsumerQueue();
                        logger.info("got message from the MQ!, consuming queue is: " + consumerQueue);
                        //TODO add routing for different consumer queues as per module
                        for(MqListener listener : mqListeners){
                            if(consumerQueue.equals(listener.getConsumerQueue())){
                                logger.info("Found listener for the mq message: " + listener.getClass());
                                listener.onMessage(message);
                            }
                        }
                    }
                };
            }
        };
        listenerEndpoint.setQueueNames(props.getQueues().toArray(new String[0])); //Get them form configuration properties
        listenerEndpoint.setId("endpoint1"); //You can keep it this way
        registrar.registerEndpoint(listenerEndpoint);
    }
}
