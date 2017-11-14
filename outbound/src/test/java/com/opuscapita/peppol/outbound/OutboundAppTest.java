package com.opuscapita.peppol.outbound;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.outbound.controller.OutboundController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by bambr on 16.15.12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = OutboundAppTestConfig.class)
public class OutboundAppTest {
    @Autowired
    private OutboundController outboundController;

    private Endpoint endpoint = new Endpoint("test", ProcessType.TEST);

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testItAll() throws Exception {
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

        cms.forEach(cm -> assertNotNull(cm.getProcessingInfo().getTransactionId()));
    }

    @Test
    public void testError() throws Exception {
        ContainerMessage cm = createContainerMessage();
        cm.setFileName("-fail-me-");

        try {
            outboundController.send(cm);
            fail("Exception wasn't propagated to calling method");
        } catch (Exception ignore) {}
    }

    private ContainerMessage createContainerMessage() {
        ContainerMessage cm = new ContainerMessage("meatdata", "file.xml", endpoint);
        DocumentInfo di = new DocumentInfo();
        cm.setDocumentInfo(di);
        return cm;
    }
}