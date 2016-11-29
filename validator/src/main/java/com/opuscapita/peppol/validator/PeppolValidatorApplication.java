package com.opuscapita.peppol.validator;

import com.google.gson.Gson;
import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.ServiceNowConfiguration;
import com.opuscapita.commons.servicenow.ServiceNowREST;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.status.StatusReporter;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.template.AbstractQueueListener;
import com.opuscapita.peppol.commons.validation.ValidationResult;
import com.opuscapita.peppol.validator.validations.ValidationController;
import com.opuscapita.peppol.validator.validations.difi.DifiValidatorConfig;
import com.opuscapita.peppol.validator.validations.svefaktura1.Svefaktura1ValidatorConfig;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication(scanBasePackages = "com.opuscapita.peppol")
public class PeppolValidatorApplication {
    @Value("${peppol.component.name}")
    String componentName;
    @Value("${peppol.validator.svefaktura1.schematron.path}")
    String svefaktur1SchematronXslPath;
    @Value("${peppol.validator.svefaktura1.xsd.path}")
    String svefaktura1XsdPath;
    @Value("${peppol.validator.svefaktura1.schematron.enabled}")
    Boolean svefaktura1SchematronValidationEnabled;
    @Value("${peppol.validation.artifacts.difi.path}")
    String difiValidationArtifactsPath;
    @Value("${peppol.validation.artifacts.si.path}")
    String simplerInvoicingValidationArtifactsPath;
    @Value("${peppol.validation.artifacts.at.path}")
    String austrianValidationArtifactsPath;
    @Value("${peppol.validation.consume-queue}")
    private String queueName;
    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        try {
            SpringApplication.run(PeppolValidatorApplication.class, args);
        } catch (Exception e) {
            //Failed to launch the application
            //Try snc stuff? :)
            e.printStackTrace();
        }
    }

    @Bean
    public Svefaktura1ValidatorConfig svefaktura1ValidatorConfig() {
        return new Svefaktura1ValidatorConfig(svefaktura1SchematronValidationEnabled, svefaktur1SchematronXslPath, svefaktura1XsdPath);
    }

    @Bean
    public DifiValidatorConfig difiValidatorConfig() {
        return new DifiValidatorConfig(difiValidationArtifactsPath);
    }

    @Bean
    public DifiValidatorConfig simplerInvoicingValidatorConfig() {
        return new DifiValidatorConfig(simplerInvoicingValidationArtifactsPath);
    }

    @Bean
    public DifiValidatorConfig austrianValidatorConfig() {
        return new DifiValidatorConfig(austrianValidationArtifactsPath);
    }


    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    ServiceNowConfiguration serviceNowConfiguration() {
        return new ServiceNowConfiguration(
                environment.getProperty("snc.rest.url"),
                environment.getProperty("snc.rest.username"),
                environment.getProperty("snc.rest.password"),
                environment.getProperty("snc.bsc"),
                environment.getProperty("snc.from"),
                environment.getProperty("snc.businessGroup"));
    }

    @Bean
    public ServiceNow serviceNowRest() {
        return new ServiceNowREST(serviceNowConfiguration());
    }

    @Bean
    public TomcatEmbeddedServletContainerFactory containerFactory() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1));
        return factory;
    }

    @Bean
    AbstractQueueListener queueListener(@Nullable ErrorHandler errorHandler,
                                        @NotNull ValidationController controller, @NotNull RabbitTemplate rabbitTemplate,
                                        @NotNull StatusReporter reporter) {
        return new AbstractQueueListener(errorHandler, reporter) {
            @SuppressWarnings("ConstantConditions")
            @Override
            protected void processMessage(@NotNull ContainerMessage cm) throws Exception {

                ValidationResult validationResult = controller.validate(cm);
                cm.setValidationResult(validationResult);
                if (!validationResult.isPassed()) {
                    throw new RuntimeException("Validation failed");
                }

                String queueOut = cm.getRoute().pop();
                rabbitTemplate.convertAndSend(queueOut, cm);
                cm.setStatus(componentName, "validation passed");
                logger.info("Validation passed for" + cm.getFileName() + ", message sent to " + queueOut + " queue");
            }
        };
    }

    @SuppressWarnings("Duplicates")
    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setPrefetchCount(10);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(AbstractQueueListener receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

}
