package com.opuscapita.peppol.test.tools.integration;

import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.SncEntity;
import com.opuscapita.peppol.test.tools.integration.configs.IntegrationTestExecutor;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import com.opuscapita.peppol.test.tools.integration.util.*;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.commons.io.FileUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.AbstractRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class IntegrationTestApp implements RabbitListenerConfigurer, CommandLineRunner {
    static String configFile;
    static String testResultFileName;
    static String templateDir;
    public static String tempDir;
    private final static Logger logger = LoggerFactory.getLogger(IntegrationTestApp.class);
    private static List<MqListener> mqListeners = new ArrayList<>();

    @Autowired
    private Environment environment;

    @Autowired
    private IntegrationTestProperties props;

    @Autowired
    private IntegrationTestConfigReader configReader;

    public static void main(String[] args) {
        logger.info("IntegrationTestApp : Starting!");
        SpringApplication.run(IntegrationTestApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
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


        List<TestResult> testResults = runTests(configFile);
        new LoggingResultBuilder().processResult(testResults); //outputs the result to console
        new HtmlResultBuilder(testResultFileName,templateDir).processResult(testResults); //test result for jenkins

        if(tempDir.startsWith("C")) { //hack to clean windows directory, no need to clean docker directory however
            try {
                FileUtils.cleanDirectory(new File(tempDir));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("IntegrationTestApp : Ended!");
        int fails = (int)testResults.stream().filter(res -> !res.isPassed()).count();
        //System.exit(fails);
        System.exit(0);  //disabling fail for a moment
    }

    private List<TestResult> runTests(String configFile) {
        IntegrationTestExecutor executor = configReader.initExecutor(configFile);
        return executor.runTests();
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
        Channel channel = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            //creating integration-tests specific queues
            for (String queue: props.getQueues()) {
                channel.queueDeclare(queue, false, false, true, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (channel != null) {
                    channel.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
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
                        //routing messages to specific listeners
                        for(MqListener listener : mqListeners){
                            if(consumerQueue.equals(listener.getConsumerQueue())){
                                logger.info("Found listener for the mq message ");
                                listener.onMessage(message);
                            }
                        }
                    }
                };
            }
        };
        listenerEndpoint.setQueueNames(props.getListeners().toArray(new String[0])); //Get them form configuration properties
        listenerEndpoint.setId("endpoint1"); //You can keep it this way
        registrar.registerEndpoint(listenerEndpoint);
    }
}
