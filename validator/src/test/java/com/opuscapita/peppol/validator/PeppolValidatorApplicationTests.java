package com.opuscapita.peppol.validator;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DifiTestConfig.class)
@Ignore("Fails on shutdown Caused by: com.rabbitmq.client.ShutdownSignalException")
public class PeppolValidatorApplicationTests {

    @Test
    public void contextLoads() {
    }

}
