package com.opuscapita.peppol.validator.handler;

import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Created by bambr on 16.22.11.
 */
@ControllerAdvice
public class UploadResponseEnityExceptionHandler extends ResponseEntityExceptionHandler {
    @Autowired
    Environment environment;

    @ExceptionHandler({MultipartException.class, FileUploadBase.FileSizeLimitExceededException.class, java.lang.IllegalStateException.class})
    public ResponseEntity<Object> handleSizeExceededException(final WebRequest request, final MultipartException ex) {
        return handleExceptionInternal(
                ex,
                "Uploaded file size exceeds the limit " + environment.getProperty("spring.http.multipart.max-file-size"),
                new HttpHeaders(),
                HttpStatus.PAYLOAD_TOO_LARGE,
                request
        );
    }
}
