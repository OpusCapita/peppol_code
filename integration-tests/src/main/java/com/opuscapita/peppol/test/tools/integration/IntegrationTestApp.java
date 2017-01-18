package com.opuscapita.peppol.test.tools.integration;

import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.SncEntity;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.status.StatusReporter;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.commons.template.AbstractQueueListener;
import com.opuscapita.peppol.test.tools.integration.configs.IntegrationTestConfig;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import com.opuscapita.peppol.test.tools.integration.util.IntegrationTestConfigReader;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;


/**
 * Created by gamanse1 on 2016.11.14..
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.opuscapita.peppol.commons", "com.opuscapita.peppol.test.tools.integration"})
public class IntegrationTestApp {
    private final static Logger logger = LogManager.getLogger(IntegrationTestApp.class);
    static String configFile;
    static String testResultFileName;
    static String templateDir;
    public static String tempDir;

    @Autowired
    private Environment environment;

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

        IntegrationTestConfig config = new IntegrationTestConfigReader(configFile).initConfig();
        List<TestResult> testResults = config.runTests();
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

    @Bean
    public ServiceNow serviceNowRest() {
        return new ServiceNow() {
            @Override
            public void insert(SncEntity sncEntity) throws IOException {
                System.out.println("Inserted incident: " + sncEntity);
            }
        };
    }

    @SuppressWarnings("Duplicates")
    @Bean
    AbstractQueueListener queueListener(@Nullable ErrorHandler errorHandler,
                                        @NotNull MessageQueue messageQueue,
                                        @NotNull StatusReporter reporter) {
        return new AbstractQueueListener(errorHandler, reporter) {
            @SuppressWarnings("ConstantConditions")
            @Override
            /*message receiver and post processor*/
            protected void processMessage(@NotNull ContainerMessage cm) throws Exception {
                logger.info("Got message from MQ like really ???? :" + cm.getFileName());
            }
        };
    }

    @SuppressWarnings("Duplicates")
    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        createIntegrationTestQueue(); //need to prepare queue first

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("validation-integration-test");  //TODO how to remove this hardcode ? no idea yet
        container.setPrefetchCount(10);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    private void createIntegrationTestQueue() {
        com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
        factory.setHost("rabbitmq");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setConnectionTimeout(500);
        Connection connection = null;
        try {
            connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare("validation-integration-test", false, false, false, null);       //integration-tests queue
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Bean
    MessageListenerAdapter listenerAdapter(AbstractQueueListener receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

}
