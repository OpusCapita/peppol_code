package com.opuscapita.peppol.email.controller;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * @author Sergejs.Roze
 */
public class EmailActionsTest {

    @Test
    public void validate() {
        List<String> input = new ArrayList<>();
        EmailActions.validate(input);

        input = Arrays.asList(EmailActions.NOTHING, EmailActions.NOTHING);
        EmailActions.validate(input);

        input = Arrays.asList(EmailActions.SNC_TICKET, EmailActions.EMAIL_AP);
        EmailActions.validate(input);

        input = Arrays.asList(EmailActions.NOTHING, EmailActions.SNC_TICKET);
        try {
            EmailActions.validate(input);
            fail();
        } catch (IllegalArgumentException expected) {}

        input = Collections.singletonList(EmailActions.SNC_TICKET);
        try {
            EmailActions.validate(input);
            fail();
        } catch (IllegalArgumentException expected) {}

        input = Arrays.asList("test", EmailActions.SNC_TICKET, EmailActions.EMAIL_AP);
        try {
            EmailActions.validate(input);
            fail();
        } catch (IllegalArgumentException expected) {}
    }
}