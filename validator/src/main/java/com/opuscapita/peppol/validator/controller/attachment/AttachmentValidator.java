package com.opuscapita.peppol.validator.controller.attachment;

import com.opuscapita.peppol.commons.validation.ValidationError;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class AttachmentValidator {

    /**
     * Checks that attachment is a real BASE64 string.
     *
     * @param characters the string that pretends to be BASE64 encoded
     * @return error message if the string is not BASE64 encoded, or null if it is
     */
    @Nullable
    public ValidationError validate(@NotNull String characters) {
        if (characters.length() > 100 && Base64.isBase64(characters)) {
            return null;
        }
        return new ValidationError("Attachment is not base64 encoded")
                .withText("Attachment is not base64 encoded")
                .withFlag("fatal")
                .withIdentifier("ATTACHMENT");
    }
}
