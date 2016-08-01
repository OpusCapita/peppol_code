package com.opuscapita.peppol.events.persistence;

import com.opuscapita.peppol.events.persistence.amqp.EventQueueListener;
import com.opuscapita.peppol.events.persistence.model.MessageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventsPersistanceApplicationTests {

    String[] negativeFixtures = {
            "",
            "{}",
            "{\"transportType\":\"IN_OUT\",\"fileName\":\"203bb266-363a-4781-8d33-8be9bb4a5334.xml\",\"fileSize\":14876,\"invoiceId\":\"356041\",\"senderId\":\"9908:919095105\",\"senderCountryCode\":\"NO\",\"recipientId\":\"9908:994356550\",\"recipientName\":\"MATS MARTIN CC VEST\",\"recipientCountryCode\":\"NO\",\"invoiceDate\":\"2016-05-31\",\"dueDate\":\"2016-06-21\",\"transactionId\":\"203bb266-363a-4781-8d33-8be9bb4a5334\",\"documentType\":\"Invoice\",\"commonName\":\"O\\u003dEVRY Norge AS,CN\\u003dAPP_1000000148,C\\u003dNO\",\"sendingProtocol\":\"AS2\"}"
    };

    String[] positiveFixtures = {
            "{\"transportType\":\"IN_OUT\",\"fileName\":\"7ecd16db-bfc1-4374-9be7-3ec00a56e9f8.xml\",\"fileSize\":38497,\"invoiceId\":\"356042\",\"senderId\":\"9908:919095105\",\"senderName\":\"LAKS-- VILDTCENTRALEN AS\",\"senderCountryCode\":\"NO\",\"recipientId\":\"9908:994356550\",\"recipientName\":\"NIWA\",\"recipientCountryCode\":\"NO\",\"invoiceDate\":\"2016-05-31\",\"dueDate\":\"2016-06-21\",\"transactionId\":\"7ecd16db-bfc1-4374-9be7-3ec00a56e9f8\",\"documentType\":\"Invoice\",\"commonName\":\"O\\u003dEVRY Norge AS,CN\\u003dAPP_1000000148,C\\u003dNO\",\"sendingProtocol\":\"AS2\"}",
            "{\"transportType\":\"IN_OUT\",\"fileName\":\"203bb266-363a-4781-8d33-8be9bb4a5334.xml\",\"fileSize\":14876,\"invoiceId\":\"356041\",\"senderId\":\"9908:919095105\",\"senderName\":\"LAKS--VILDTCENTRALEN AS\",\"senderCountryCode\":\"NO\",\"recipientId\":\"9908:994356550\",\"recipientName\":\"MATS MARTIN CC VEST\",\"recipientCountryCode\":\"NO\",\"invoiceDate\":\"2016-05-31\",\"dueDate\":\"2016-06-21\",\"transactionId\":\"203bb266-363a-4781-8d33-8be9bb4a5334\",\"documentType\":\"Invoice\",\"commonName\":\"O\\u003dEVRY Norge AS,CN\\u003dAPP_1000000148,C\\u003dNO\",\"sendingProtocol\":\"AS2\"}"
    };

    @SpyBean
    EventQueueListener eventQueueListener;

    @Autowired
    MessageRepository messageRepository;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testInvalidJson() {
        Arrays.asList(negativeFixtures).forEach(negative -> assertTrue(testOutcome(negative)));
    }

    protected boolean testOutcome(String message) {
        boolean expectedExceptionCaught = false;
        try {
            willThrow(AmqpRejectAndDontRequeueException.class).given(this.eventQueueListener).handleError(anyString(), any(Exception.class));
            eventQueueListener.receiveMessage(message.getBytes());
        } catch (AmqpRejectAndDontRequeueException e) {
            expectedExceptionCaught = true;
        }
        return expectedExceptionCaught;
    }

    @Test
    public void testGoodJson() {
        long start = messageRepository.count();
        System.out.println("Initial number of messages: "+start);
        Arrays.asList(positiveFixtures).forEach(positive -> assertFalse(testOutcome(positive)));
        long end = messageRepository.count();
        System.out.println("Final number of messages: "+end);
        System.out.println("Expected number: "+ (start + positiveFixtures.length));
        assertEquals(start + positiveFixtures.length, end);
    }

}
