package com.opuscapita.peppol.test.tools.integration.util;

import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for creating nice HTML check result file for jenkins, it uses summary.ftl and template.ftl templates located in
 * resources/html
 */
public class HtmlResultBuilder implements ResultBuilder{

	private Configuration configuration;
	private String templateDirectory;
	private String testResultFileName;
	private Logger logger = LoggerFactory.getLogger(HtmlResultBuilder.class);

	public HtmlResultBuilder(String testResultFileName, String templateDirectory) {
		this.testResultFileName = testResultFileName;
        this.templateDirectory = templateDirectory;
        loadConfiguration();
	}

	public void loadConfiguration() {
		configuration = new Configuration(Configuration.VERSION_2_3_21);
		File templateDirectoryFile = new File(templateDirectory);
		logger.info("Template directory path: " + templateDirectory);
		try {
			configuration.setDirectoryForTemplateLoading(templateDirectoryFile);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Unable to create HtmlResultBuilder, " + e);
		}
		configuration.setDefaultEncoding("UTF-8");
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	}

	@Override
	public void processResult(List<TestResult> testResults) {
        if(testResults == null || testResults.size() < 1)
            return;

        List<TestResult> passedTests = testResults.stream().filter(c -> c.isPassed()).collect(Collectors.toList());
        List<TestResult> failedTests = testResults.stream().filter(c -> !c.isPassed()).collect(Collectors.toList());

        String htmlContent = buildHtmlTestsSummaryString(testResults, passedTests, failedTests);

		try(PrintWriter writer = new PrintWriter(testResultFileName)){
			writer.println(htmlContent);
            logger.info("HtmlResultBuilder:" + ": result file created: " + testResultFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
            logger.error("HtmlResultBuilder:" + ": Error saving result file " + e);
		}
	}

    private String buildHtmlTestsSummaryString(List<TestResult> allTests, List<TestResult> passedTests, List<TestResult> failedTests) {
        Template template = null;
        try {
            template = configuration.getTemplate("summary.ftl");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Writer out = new OutputStreamWriter(outputStream);
        Date now = new Date();

        Map<String, Object> data = new HashMap<>();
        data.put("result", failedTests.size() > 0 ? "FAILURE" : "SUCCESS");
        data.put("all_tests", allTests.size());
        data.put("successful_tests", passedTests.size());
        data.put("failed_tests", failedTests.size());
        data.put("time", DateFormat.getInstance().format(now));
        data.put("percentage", Double.valueOf(((double) passedTests.size()/ (double) allTests.size()) * 100.0));
        data.put("comment", "integration test checks");
        data.put("item", new Object());

        List<Object> failures = buildCheckSection(failedTests);
        List<Object> oks = buildCheckSection(passedTests);

        if(oks != null )
            data.put("oks",oks);
        if(failures != null)
            data.put("fails", failures);
        try {
            template.process(data, out);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(HtmlResultBuilder.class + ": Error when processing check results into template " + e);
        }
        return outputStream.toString();
    }

    private List<Object> buildCheckSection(List<TestResult> testresults){
        if(testresults == null || testresults.size() < 1)
            return null;

        List<Object> out = new ArrayList<>();
        for(TestResult testResult : testresults){
            Map<String, Object> summaryPair = new HashMap<>();

            List<Map<String, Object>> assertList = new ArrayList<>();
            List<Map<String, Object>> summaryList = new ArrayList<>();
            Map<String, Object> testMap = new HashMap<>();
            Map<String, Object> summaryMap = new HashMap<>();

            assertList.add(createAssertRow(testResult));

            testMap.put("name", "name");
            testMap.put("asserts", assertList);

            summaryMap.put("details", testMap);

            summaryList.add(summaryMap);

            summaryPair.put("name", testResult.getName());
            summaryPair.put("value",summaryList);
            out.add(summaryPair);
        }
        return out;
    }

    private Map<String, Object> createAssertRow(TestResult testResult) {
        Map<String, Object> assertMap = new HashMap<>();
        assertMap.put("name", testResult.getName());
        assertMap.put("comment",testResult.getDetails());
        assertMap.put("expected_result", "Passed");
        assertMap.put("output_value", testResult.isPassed() ? "Passed" : "Failed");
        return assertMap;
    }
}
