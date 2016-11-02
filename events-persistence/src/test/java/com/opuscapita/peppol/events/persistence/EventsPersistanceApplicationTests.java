package com.opuscapita.peppol.events.persistence;

import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.ServiceNowConfiguration;
import com.opuscapita.commons.servicenow.SncEntity;
import com.opuscapita.peppol.events.persistence.amqp.EventQueueListener;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
@Configuration
@ComponentScan(excludeFilters = @ComponentScan.Filter(value = EventQueueListener.class, type = FilterType.ASSIGNABLE_TYPE))
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
public class EventsPersistanceApplicationTests {
    Logger logger = Logger.getLogger(EventsPersistanceApplicationTests.class);
    String[] negativeFixtures = {
            "",
            "{}",
            /*"{\"transportType\":\"OUT_VALIDATE\",\"fileName\":\"203bb266-363a-4781-8d33-8be9bb4a5334.xml\",\"fileSize\":14876,\"invoiceId\":\"356041\",\"senderId\":\"9908:919095105\",\"senderCountryCode\":\"NO\",\"recipientId\":\"9908:994356550\",\"recipientName\":\"MATS MARTIN CC VEST\",\"recipientCountryCode\":\"NO\",\"invoiceDate\":\"2016-05-31\",\"dueDate\":\"2016-06-21\",\"transactionId\":\"203bb266-363a-4781-8d33-8be9bb4a5334\",\"documentType\":\"Invoice\",\"commonName\":\"O\\u003dEVRY Norge AS,CN\\u003dAPP_1000000148,C\\u003dNO\",\"sendingProtocol\":\"AS2\"}",
            "{\"transportType\":\"IN_OUT\",\"fileName\":\"203bb266-363a-4781-8d33-8be9bb4a5334.xml\",\"fileSize\":14876,\"invoiceIdWoot\":\"\",\"senderIdWoot\":\"\",\"senderCountryCode\":\"NO\",\"recipientIdWoot\":\"\",\"recipientCountryCode\":\"NO\",\"invoiceDate\":\"2016-05-31\",\"dueDate\":\"2016-06-21\",\"transactionId\":\"203bb266-363a-4781-8d33-8be9bb4a5334\",\"documentType\":\"Invoice\",\"commonName\":\"O\\u003dEVRY Norge AS,CN\\u003dAPP_1000000148,C\\u003dNO\",\"sendingProtocol\":\"AS2\"}"*/
    };
    String[] positiveFixtures = {
            "{\"transportType\":\"IN_OUT\",\"fileName\":\"7ecd16db-bfc1-4374-9be7-3ec00a56e9f8.xml\",\"fileSize\":38497,\"invoiceId\":\"356042\",\"senderId\":\"9908:919095105\",\"senderName\":\"LAKS-- VILDTCENTRALEN AS\",\"senderCountryCode\":\"NO\",\"recipientId\":\"9908:994356550\",\"recipientName\":\"NIWA\",\"recipientCountryCode\":\"NO\",\"invoiceDate\":\"2016-05-31\",\"dueDate\":\"2016-06-21\",\"transactionId\":\"7ecd16db-bfc1-4374-9be7-3ec00a56e9f8\",\"documentType\":\"Invoice\",\"commonName\":\"O\\u003dEVRY Norge AS,CN\\u003dAPP_1000000148,C\\u003dNO\",\"sendingProtocol\":\"AS2\"}",
            "{\"transportType\":\"IN_OUT\",\"fileName\":\"203bb266-363a-4781-8d33-8be9bb4a5334.xml\",\"fileSize\":14876,\"invoiceId\":\"356041\",\"senderId\":\"9908:919095105\",\"senderName\":\"LAKS--VILDTCENTRALEN AS\",\"senderCountryCode\":\"NO\",\"recipientId\":\"9908:994356550\",\"recipientName\":\"MATS MARTIN CC VEST\",\"recipientCountryCode\":\"NO\",\"invoiceDate\":\"2016-05-31\",\"dueDate\":\"2016-06-21\",\"transactionId\":\"203bb266-363a-4781-8d33-8be9bb4a5334\",\"documentType\":\"Invoice\",\"commonName\":\"O\\u003dEVRY Norge AS,CN\\u003dAPP_1000000148,C\\u003dNO\",\"sendingProtocol\":\"AS2\"}"
    };

    @Bean
    @ConditionalOnMissingBean(ServiceNowConfiguration.class)
    ServiceNowConfiguration serviceNowConfiguration() {
        return null;
    }

    @Bean
    @ConditionalOnMissingBean(ServiceNow.class)
    ServiceNow serviceNowRest() {
        return new ServiceNow() {
            @Override
            public void insert(SncEntity sncEntity) throws IOException {
                logger.info("Inserted " + sncEntity.toString());
            }
        };
    }

    @Test
    public void contextLoads() {
    }

}
