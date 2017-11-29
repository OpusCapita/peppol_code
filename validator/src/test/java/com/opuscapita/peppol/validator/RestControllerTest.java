package com.opuscapita.peppol.validator;

import com.opuscapita.peppol.commons.validation.ValidationResult;
import com.opuscapita.peppol.validator.rest.RestValidator;
import com.opuscapita.peppol.validator.validations.common.TestCommon;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestTestConfig.class)
public class RestControllerTest extends TestCommon {
    @Value("${server.port}")
    Integer port;

    @Autowired
    RestValidator restValidator;

    @Test
    public void testRestControllerWarningsParsing() throws IOException {
        ValidationResult response = getValidationResult("/test_data/simpler_invoicing_files/invalids-wrapped.Kone-test-OIN.xml");
        assertTrue(response.getErrors().size() == 1);
        assertTrue(response.getWarnings().size() > 0);
    }

    @Test
    public void testCorrectRestValidation() throws IOException {
        ValidationResult response = getValidationResult("/test_data/simpler_invoicing_files/invalids-wrapped.Kone-test-OIN.xml");
    }

    @NotNull
    protected ValidationResult getValidationResult(String pathToResource) throws IOException {
        String fileName = "fake";
        byte[] fileContent = IOUtils.toByteArray(this.getClass().getResourceAsStream(pathToResource));
        MultipartFile multipartFile = new MockMultipartFile(fileName, fileContent);
        ValidationResult response = restValidator.validate(multipartFile, false);
        assertNotNull(response);
        return response;
    }
}
