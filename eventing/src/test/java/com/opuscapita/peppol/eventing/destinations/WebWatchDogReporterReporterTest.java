package com.opuscapita.peppol.eventing.destinations;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.ProcessingInfo;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.eventing.destinations.webwatchdog.WebWatchDogConfig;
import com.opuscapita.peppol.eventing.destinations.webwatchdog.WebWatchDogMessenger;
import org.apache.commons.io.FileUtils;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by bambr on 17.7.6.
 */
public class WebWatchDogReporterReporterTest {
    private static final String MEATDATA = "MEATDATA->TRIBUTE TO HOLY KACEN";
    private static File tmpDir;
    WebWatchDogReporterReporter webWatchDogReporterReporter = null;
    private Endpoint endpoint;
    private ProcessingInfo processingInfo;

    @BeforeClass
    public static void setupTmpDir() throws IOException {
        tmpDir = Files.createTempDirectory("wwd").toFile();
    }

    @AfterClass
    public static void removeTmpDir() throws IOException {
        FileUtils.cleanDirectory(tmpDir);
        FileUtils.forceDelete(tmpDir);
    }

    @Before
    public void setUp() throws Exception {
        WebWatchDogConfig webWatchDogConfig = new WebWatchDogConfig(tmpDir.getAbsolutePath(), "wwd");
        WebWatchDogMessenger webWatchDogMessenger = new WebWatchDogMessenger(webWatchDogConfig);
        webWatchDogReporterReporter = new WebWatchDogReporterReporter(webWatchDogMessenger);
        endpoint = new Endpoint("test", ProcessType.OUT_OUTBOUND);
        processingInfo = new ProcessingInfo(endpoint, MEATDATA);
        processingInfo.setCurrentStatus(endpoint, "w00t");

    }

    @After
    public void tearDown() throws Exception {
        webWatchDogReporterReporter = null;
        endpoint = null;
        processingInfo = null;
    }

    @Test
    public void process() throws Exception {
        assertNotNull(tmpDir);
        assertNotNull(webWatchDogReporterReporter);
        assertEquals(0, Arrays.asList(tmpDir.listFiles()).size());
        ContainerMessage positiveCaseShouldTriggerWwd = new ContainerMessage(MEATDATA, "logger_test.xml", endpoint);
        positiveCaseShouldTriggerWwd.setProcessingInfo(processingInfo);
        webWatchDogReporterReporter.process(positiveCaseShouldTriggerWwd);
        assertEquals(1, Arrays.asList(tmpDir.listFiles()).size());
    }

}