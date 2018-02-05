package com.opuscapita.peppol.transport;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.ContainerMessageSerializer;
import com.opuscapita.peppol.commons.container.process.StatusReporter;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.template.AbstractQueueListener;
import com.opuscapita.peppol.transport.controller.TransportController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * @author Daniil Kalnin
 */
@SpringBootApplication(scanBasePackages = {"com.opuscapita.peppol.commons", "com.opuscapita.peppol.transport"})
@EnableDiscoveryClient
public class MessageQueueToJmsApp {
    @Value("${peppol.jms.destination}")
    String jmsDestination;

    @Value(("${peppol.jms.user}"))
    String jmsUser;

    @Value("${peppol.jms.password}")
    String jmsPassword;

    @Autowired
    Session session;

    @Autowired
    Topic topic;

    @Autowired
    Connection connection;


    @Value("${peppol.component.name}")
    private String componentName;
    @Value("${peppol.mq-to-jms.queue.in.name}")
    private String queueIn;

    public static void main(String[] args) {
        SpringApplication.run(MessageQueueToJmsApp.class, args);
    }

    @Bean
    public Context context(Environment environment,
                           @Value("${java.naming.factory.initial}") String initialContextConf,
                           @Value("${connectionfactory.qpidConnectionFactory}") String connectionFactoryConf,
                           @Value("topic.peppol") String topicConf
    ) throws Exception {
        Properties properties = new Properties();
        /*Iterator<PropertySource<?>> iterator = ((AbstractEnvironment) environment)
                .getPropertySources()
                .iterator();
        while (iterator.hasNext()) {
            PropertySource propertySource = (PropertySource) iterator.next();
            if(propertySource instanceof MapPropertySource) {
                properties.putAll(((MapPropertySource)propertySource).getSource());
            }
        }
*/
        properties.put("java.naming.factory.initial", initialContextConf);
        properties.put("connectionfactory.qpidConnectionFactory", connectionFactoryConf);
        properties.put("topic.peppol", topicConf);
        properties.list(System.out);
        return new InitialContext(properties);
    }

    @Bean
    public Topic topic(Context context) throws NamingException {
        return (Topic) context.lookup("peppol");
    }

    @Bean
    public Connection connection(Context context) throws Exception {
        ConnectionFactory connectionFactory
                = (ConnectionFactory) context.lookup("qpidConnectionFactory");
        Connection connection = connectionFactory.createConnection();
        connection.start();
        return connection;
    }


    @Bean
    public Session session(Connection connection) throws JMSException {
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @Bean
    public MessageProducer messageProducer(Session session, Topic topic) throws JMSException {
        return session.createProducer(topic);
    }

    @SuppressWarnings("Duplicates")
    @Bean
    SimpleMessageListenerContainer container(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueIn);
        container.setPrefetchCount(10);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    AbstractQueueListener queueListener(@Nullable ErrorHandler errorHandler, @NotNull StatusReporter reporter,
                                        @NotNull TransportController controller, @NotNull ContainerMessageSerializer serializer) {
        return new AbstractQueueListener(errorHandler, reporter, serializer) {
            @Override
            protected void processMessage(@NotNull ContainerMessage cm) throws Exception {
                logger.info("Storing incoming message: " + cm.getFileName());
                controller.send(cm);
                cm.setStatus(new Endpoint(componentName, ProcessType.IN_MQ_TO_FILE), "delivered");
            }
        };
    }

    @Bean
    MessageListenerAdapter listenerAdapter(AbstractQueueListener receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

}
