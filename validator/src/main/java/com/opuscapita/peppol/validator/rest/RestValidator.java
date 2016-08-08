package com.opuscapita.peppol.validator.rest;

import com.opuscapita.peppol.validator.validations.common.ValidationResult;
import com.opuscapita.peppol.validator.validations.common.ValidationType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniil on 03.05.2016.
 */
@RestController("/rest")
public class RestValidator {
    @RequestMapping(value = "/difi", method = RequestMethod.POST)
    public ValidationResult validate(@RequestParam(value = "type", defaultValue = "difi") String type, @RequestParam("file") MultipartFile file) {
        ValidationType validationType;
        switch(type.trim().toLowerCase()) {
            case "svefaktura1":
                validationType = ValidationType.SVEFAKTURA1;
                break;
            case "difi":
            default:
                validationType = ValidationType.DIFI;
        }
        ValidationResult result = new ValidationResult(validationType);
        result.setPassed(true);
        return result;
    }
}
