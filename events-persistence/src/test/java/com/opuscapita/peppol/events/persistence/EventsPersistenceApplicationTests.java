package com.opuscapita.peppol.events.persistence;

import com.google.gson.Gson;
import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.ServiceNowConfiguration;
import com.opuscapita.peppol.commons.model.PeppolEvent;
import com.opuscapita.peppol.events.persistence.amqp.EventQueueListener;
import com.opuscapita.peppol.events.persistence.controller.PersistenceController;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Configuration
@ComponentScan(excludeFilters = @ComponentScan.Filter(value = EventQueueListener.class, type = FilterType.ASSIGNABLE_TYPE))
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
public class EventsPersistenceApplicationTests {
    String[] negativeFixtures = {
            "",
            "{}",
            /*"{\"transportType\":\"OUT_VALIDATE\",\"fileName\":\"203bb266-363a-4781-8d33-8be9bb4a5334.xml\",\"fileSize\":14876,\"invoiceId\":\"356041\",\"senderId\":\"9908:919095105\",\"senderCountryCode\":\"NO\",\"recipientId\":\"9908:994356550\",\"recipientName\":\"MATS MARTIN CC VEST\",\"recipientCountryCode\":\"NO\",\"invoiceDate\":\"2016-05-31\",\"dueDate\":\"2016-06-21\",\"transactionId\":\"203bb266-363a-4781-8d33-8be9bb4a5334\",\"documentType\":\"Invoice\",\"commonName\":\"O\\u003dEVRY Norge AS,CN\\u003dAPP_1000000148,C\\u003dNO\",\"sendingProtocol\":\"AS2\"}",
            "{\"transportType\":\"IN_OUT\",\"fileName\":\"203bb266-363a-4781-8d33-8be9bb4a5334.xml\",\"fileSize\":14876,\"invoiceIdWoot\":\"\",\"senderIdWoot\":\"\",\"senderCountryCode\":\"NO\",\"recipientIdWoot\":\"\",\"recipientCountryCode\":\"NO\",\"invoiceDate\":\"2016-05-31\",\"dueDate\":\"2016-06-21\",\"transactionId\":\"203bb266-363a-4781-8d33-8be9bb4a5334\",\"documentType\":\"Invoice\",\"commonName\":\"O\\u003dEVRY Norge AS,CN\\u003dAPP_1000000148,C\\u003dNO\",\"sendingProtocol\":\"AS2\"}"*/
    };
    String[] positiveFixtures = {
            "{\"transportType\":\"IN_OUT\",\"fileName\":\"7ecd16db-bfc1-4374-9be7-3ec00a56e9f8.xml\",\"fileSize\":38497,\"invoiceId\":\"356042\",\"senderId\":\"9908:919095105\",\"senderName\":\"LAKS-- VILDTCENTRALEN AS\",\"senderCountryCode\":\"NO\",\"recipientId\":\"9908:994356550\",\"recipientName\":\"NIWA\",\"recipientCountryCode\":\"NO\",\"invoiceDate\":\"2016-05-31\",\"dueDate\":\"2016-06-21\",\"transactionId\":\"7ecd16db-bfc1-4374-9be7-3ec00a56e9f8\",\"documentType\":\"Invoice\",\"commonName\":\"O\\u003dEVRY Norge AS,CN\\u003dAPP_1000000148,C\\u003dNO\",\"sendingProtocol\":\"AS2\"}",
            "{\"transportType\":\"IN_OUT\",\"fileName\":\"203bb266-363a-4781-8d33-8be9bb4a5334.xml\",\"fileSize\":14876,\"invoiceId\":\"356041\",\"senderId\":\"9908:919095105\",\"senderName\":\"LAKS--VILDTCENTRALEN AS\",\"senderCountryCode\":\"NO\",\"recipientId\":\"9908:994356550\",\"recipientName\":\"MATS MARTIN CC VEST\",\"recipientCountryCode\":\"NO\",\"invoiceDate\":\"2016-05-31\",\"dueDate\":\"2016-06-21\",\"transactionId\":\"203bb266-363a-4781-8d33-8be9bb4a5334\",\"documentType\":\"Invoice\",\"commonName\":\"O\\u003dEVRY Norge AS,CN\\u003dAPP_1000000148,C\\u003dNO\",\"sendingProtocol\":\"AS2\"}",
            "{\n" +
                    "  \"transportType\":\"IN_MQ_TO_FILE\",\n" +
                    "  \"fileName\":\"/peppol/data/storage/9908_995483254/9908_915141668/20170904/804b67da-1c78-4b40-afa1-5c93b2411697.xml\",\n" +
                    "  \"fileSize\":348384,\n" +
                    "  \"errorFilePath\":\"null\",\n" +
                    "  \"invoiceId\":\"103459\",\n" +
                    "  \"senderId\":\"9908:995483254\",\n" +
                    "  \"senderName\":\"Sahaga AS\",\n" +
                    "  \"senderCountryCode\":\"NO\",\n" +
                    "  \"recipientId\":\"9908:915141668\",\n" +
                    "  \"recipientName\":\"GUSTAV PEDERSEN AS NORENGROS\",\n" +
                    "  \"recipientCountryCode\":\"NO\",\n" +
                    "  \"invoiceDate\":\"2017-08-30\",\n" +
                    "  \"dueDate\":\"2017-09-19\",\n" +
                    "  \"errorMessage\":\"\",\n" +
                    "  \"transactionId\":\"null\",\n" +
                    "  \"documentType\":\"EHF Invoice\",\n" +
                    "  \"commonName\":\"null\",\n" +
                    "  \"sendingProtocol\":\"null\"\n" +
                    "}"
    };
    @Autowired
    private PersistenceController persistenceController;
    private Logger logger = Logger.getLogger(EventsPersistenceApplicationTests.class);

    @Bean
    @ConditionalOnMissingBean(ServiceNowConfiguration.class)
    ServiceNowConfiguration serviceNowConfiguration() {
        return null;
    }

    @Bean
    @ConditionalOnMissingBean(ServiceNow.class)
    ServiceNow serviceNowRest() {
        return sncEntity -> logger.info("Inserted " + sncEntity.toString());
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void testStorePeppolEvent() {
        String[] testDataFiles = {"/OCIN00871839_Norengros_test_case.json", "/Invalid-msg-field-lenghts-and-corrupted-contents-negative-test.json", "/a2a_delivered.json"};

        Gson gson = new Gson();
        Arrays.asList(testDataFiles).forEach(testDataFile -> {
            try {
                PeppolEvent peppolEvent = gson.fromJson(new BufferedReader(new InputStreamReader(EventsPersistenceApplicationTests.class.getResource(testDataFile).openStream())), PeppolEvent.class);
                System.out.println("File: " + peppolEvent.getFileName() + " -> " + peppolEvent.getProcessType() );
                persistenceController.storePeppolEvent(peppolEvent);
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        });

    }

}
