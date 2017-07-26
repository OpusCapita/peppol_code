package com.opuscapita.peppol.eventing.destinations;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.ProcessingInfo;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.eventing.destinations.webwatchdog.WebWatchDogConfig;
import com.opuscapita.peppol.eventing.destinations.webwatchdog.WebWatchDogMessenger;
import com.opuscapita.peppol.eventing.destinations.webwatchdog.WebWatchDogStatus;
import org.apache.commons.io.FileUtils;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by bambr on 17.7.6.
 */
@SuppressWarnings("ConstantConditions")
public class WebWatchDogReporterTest {
    private static final String MEATDATA = "MEATDATA->TRIBUTE TO HOLY KACEN";
    private static File tmpDir;
    private WebWatchDogReporter webWatchDogReporter = null;
    private Endpoint endpoint;
    private ProcessingInfo processingInfo;
    private ContainerMessage containerMessage;

    @BeforeClass
    public static void setupTmpDir() throws IOException {
        tmpDir = Files.createTempDirectory("wwd").toFile();
    }

    @AfterClass
    public static void removeTmpDir() throws IOException {
        FileUtils.forceDelete(tmpDir);
    }

    @Before
    public void setUp() throws Exception {
        WebWatchDogConfig webWatchDogConfig = new WebWatchDogConfig(tmpDir.getAbsolutePath(), "wwd");
        WebWatchDogMessenger webWatchDogMessenger = new WebWatchDogMessenger(webWatchDogConfig);
        webWatchDogReporter = new WebWatchDogReporter(webWatchDogMessenger);
        endpoint = new Endpoint("test", ProcessType.OUT_OUTBOUND);
        processingInfo = new ProcessingInfo(endpoint, MEATDATA);
        processingInfo.setCurrentStatus(endpoint, "w00t");
        containerMessage = new ContainerMessage(MEATDATA, "logger_test.xml", endpoint);
        FileUtils.cleanDirectory(tmpDir);
    }

    @After
    public void tearDown() throws Exception {
        webWatchDogReporter = null;
        endpoint = null;
        processingInfo = null;
        containerMessage = null;
    }

    @Test
    public void testOutboundNegative() throws Exception {
        //Checking initial state of directory we use, it must be empty
        assertEquals(0, Arrays.asList(tmpDir.listFiles()).size());

        //Creating container message, initially endpoint is having testOutboundNegative type of OUT_OUTBOUND
        containerMessage.setProcessingInfo(processingInfo);
        webWatchDogReporter.process(containerMessage);
        //We should be having only one file
        assertEquals(1, Arrays.asList(tmpDir.listFiles()).size());
        //It should contain FAILED status
        assertTrue(FileUtils.readFileToString(tmpDir.listFiles()[0], "UTF-8").contains(WebWatchDogStatus.FAILED));
    }

    @Test
    public void testOutboundPositive() throws IOException {
        //Checking initial state of directory we use, it must be empty
        assertEquals(0, Arrays.asList(tmpDir.listFiles()).size());
        //Changing status for container message to delivered
        processingInfo.setCurrentStatus(endpoint, "delivered");
        containerMessage.setProcessingInfo(processingInfo);
        webWatchDogReporter.process(containerMessage);
        //We should be having only one file
        assertEquals(1, Arrays.asList(tmpDir.listFiles()).size());
        //It should contain OK status
        assertTrue(FileUtils.readFileToString(tmpDir.listFiles()[0], "UTF-8").contains(WebWatchDogStatus.OK));
    }

    @Test
    public void testValidationNegative() throws IOException {
        //Checking initial state of directory we use, it must be empty
        assertEquals(0, Arrays.asList(tmpDir.listFiles()).size());
        //Creating new endpoint to check OUT_VALIDATION case, only failures reported
        endpoint = new Endpoint("test", ProcessType.OUT_VALIDATION);
        processingInfo.setCurrentStatus(endpoint, "YOU SHALL NOT PASS!!!");
        containerMessage.setProcessingInfo(processingInfo);
        webWatchDogReporter.process(containerMessage);
        assertEquals(1, Arrays.asList(tmpDir.listFiles()).size());
        assertTrue(FileUtils.readFileToString(tmpDir.listFiles()[0], "UTF-8").contains(WebWatchDogStatus.INVALID));
    }

    @Test
    public void testValidationPositive() throws IOException {
        //Checking initial state of directory we use, it must be empty
        assertEquals(0, Arrays.asList(tmpDir.listFiles()).size());
        //Creating new endpoint to check OUT_VALIDATION case, only failures reported
        endpoint = new Endpoint("test", ProcessType.OUT_VALIDATION);
        processingInfo.setCurrentStatus(endpoint, "validation passed");
        containerMessage.setProcessingInfo(processingInfo);
        webWatchDogReporter.process(containerMessage);
        assertEquals(0, Arrays.asList(tmpDir.listFiles()).size());
    }

    @Test
    public void testApplicabilityNegative() {
        //Checking initial state of directory we use, it must be empty
        assertEquals(0, Arrays.asList(tmpDir.listFiles()).size());
        containerMessage = new ContainerMessage(MEATDATA, "not_a_logger.xml", endpoint);
        containerMessage.setProcessingInfo(processingInfo);
        webWatchDogReporter.process(containerMessage);
        assertEquals(0, Arrays.asList(tmpDir.listFiles()).size());
    }

}