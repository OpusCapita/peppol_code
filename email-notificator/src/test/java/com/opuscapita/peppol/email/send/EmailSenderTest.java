package com.opuscapita.peppol.email.send;

import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;

import static org.easymock.EasyMock.mock;
import static org.junit.Assert.assertEquals;

/**
 * @author Sergejs.Roze
 */
public class EmailSenderTest {

    @Test
    public void getRecipients() {
        EmailSender es = new EmailSender(mock(JavaMailSender.class));

        String[] result = es.getRecipients("test@test.a");
        assertEquals(1, result.length);
        assertEquals("test@test.a", result[0]);

        result = es.getRecipients("test@test.a test@test.b");
        assertEquals(2, result.length);
        assertEquals("test@test.a", result[0]);
        assertEquals("test@test.b", result[1]);

        result = es.getRecipients("test@test.a,test@test.b");
        assertEquals(2, result.length);
        assertEquals("test@test.a", result[0]);
        assertEquals("test@test.b", result[1]);

        result = es.getRecipients("test@test.a;test@test.b");
        assertEquals(2, result.length);
        assertEquals("test@test.a", result[0]);
        assertEquals("test@test.b", result[1]);

        result = es.getRecipients(" test@test.a; test@test.b ");
        assertEquals(2, result.length);
        assertEquals("test@test.a", result[0]);
        assertEquals("test@test.b", result[1]);

        result = es.getRecipients("test@test.a test@test.b; test@test.c , test@test.d");
        assertEquals(4, result.length);
        assertEquals("test@test.a", result[0]);
        assertEquals("test@test.b", result[1]);
        assertEquals("test@test.c", result[2]);
        assertEquals("test@test.d", result[3]);
    }

}