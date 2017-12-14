package com.opuscapita.peppol.outbound;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.process.StatusReporter;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.outbound.controller.OutboundController;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by bambr on 16.15.12.
 */
@Ignore("Cannot run properly anymore due to introduction of async methods")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = OutboundAppTestConfig.class)
public class OutboundAppTest {
    @Autowired
    private OutboundController outboundController;

    static StatusReporter statusReporter = mock(StatusReporter.class);
    static ErrorHandler errorHandler = mock(ErrorHandler.class);

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testItAll() throws InterruptedException {
        List<ContainerMessage> cms = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            cms.add(createContainerMessage());
        }

        cms.parallelStream().forEach(cm1 -> {
            try {
                outboundController.send(cm1);
            } catch (Exception e) {
                fail("Unexpected failure in fake sending");
            }
        });
        Thread.sleep(10000);

        cms.forEach(cm -> assertNotNull(cm.getProcessingInfo().getTransactionId()));
    }

    @Test
    public void testError() throws Exception {
        ContainerMessage cm = createContainerMessage();
        cm.setFileName("-fail-me-");

        outboundController.send(cm);
        Thread.sleep(10000);

        verify(errorHandler).reportWithContainerMessage(eq(cm), any(), any());
    }

    private ContainerMessage createContainerMessage() {
        ContainerMessage cm = new ContainerMessage("meatdata", "file.xml", Endpoint.TEST);
        DocumentInfo di = new DocumentInfo();
        cm.setDocumentInfo(di);
        return cm;
    }

}