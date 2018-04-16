package com.opuscapita.peppol.validator.controller.attachment;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Sergejs.Roze
 */
public class AttachmentValidatorTest {

    @Test
    public void isValidBase64() {
        AttachmentValidator av = new AttachmentValidator();

        assertFalse(av.isValidBase64(""));
        assertFalse(av.isValidBase64("YW55IGNhcm5hbCBwbGVhc==="));
        assertFalse(av.isValidBase64("YW55IGNhcm5hbC=BwbGVhcw="));

        assertTrue(av.isValidBase64("YW55IGNhcm5hbCBwbGVhcw=="));
        assertTrue(av.isValidBase64("YW55IGNhcm5hbCBwbGVhcw==\n      "));
        assertTrue(av.isValidBase64("YW55IGNhcm5hbCBwbGVhcw==\n       "));
        assertTrue(av.isValidBase64("YW55IGNhcm5hbCBwbGVhc3U="));
        assertTrue(av.isValidBase64("YW55IGNhcm5hbCBwbGVhc3Vy"));
        assertTrue(av.isValidBase64("YW55IGNhcm5hbCBwbGVhc3Vy\n"));
        assertTrue(av.isValidBase64(" YW55IGNhcm5h\n   bCBwbGVhc3Vy\n    "));
    }
}