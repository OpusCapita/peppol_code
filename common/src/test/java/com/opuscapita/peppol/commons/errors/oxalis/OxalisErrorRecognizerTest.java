package com.opuscapita.peppol.commons.errors.oxalis;

import org.junit.Test;

import java.util.Arrays;

import static com.opuscapita.peppol.commons.errors.oxalis.SendingErrors.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Sergejs.Roze
 */
public class OxalisErrorRecognizerTest {

    @Test
    public void testRecognition() throws Exception {
        OxalisErrorsList errorsList = new OxalisErrorsList();
        errorsList.setList(Arrays.asList(
                new OxalisError(DATA_ERROR, "^wrong data$"),
                new OxalisError(SECURITY_ERROR, ".*certificate expired.*")
        ));

        OxalisErrorRecognizer recognizer = new OxalisErrorRecognizer(errorsList);

        assertEquals(OTHER_ERROR, recognizer.recognize(new NullPointerException()));
        assertEquals(DATA_ERROR, recognizer.recognize("wrong data"));
        assertEquals(OTHER_ERROR, recognizer.recognize("wrong data in line 5"));
        assertEquals(SECURITY_ERROR, recognizer.recognize("hey mom my certificate expired long time ago"));
    }

}
