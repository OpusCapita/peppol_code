package com.opuscapita.peppol.test.tools.smoke.util;

import com.opuscapita.peppol.test.tools.smoke.checks.CheckResult;
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
	public void processResult(List<CheckResult> checkResults) {
        if(checkResults == null || checkResults.size() < 1)
            return;

        List<CheckResult> goodChecks = checkResults.stream().filter(c -> c.isPassed()).collect(Collectors.toList());
        List<CheckResult> badChecks =checkResults.stream().filter(c -> !c.isPassed()).collect(Collectors.toList());

        String htmlContent = buildHtmlTestsSummaryString(checkResults, goodChecks, badChecks);

		try(PrintWriter writer = new PrintWriter(testResultFileName)){
			writer.println(htmlContent);
            logger.info(HtmlResultBuilder.class + ": result file created: " + testResultFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
            logger.error(HtmlResultBuilder.class + ": Error saving result file " + e);
		}
	}

    private String buildHtmlTestsSummaryString(List<CheckResult> allChecks, List<CheckResult> goodChecks, List<CheckResult> badChecks) {
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
        data.put("result", badChecks.size() > 0 ? "FAILURE" : "SUCCESS");
        data.put("all_tests", allChecks.size());
        data.put("successful_tests", goodChecks.size());
        data.put("failed_tests", badChecks.size());
        data.put("time", DateFormat.getInstance().format(now));
        data.put("percentage", Double.valueOf(((double) goodChecks.size()/ (double) allChecks.size()) * 100.0));
        data.put("comment", "smoke test checks");
        data.put("item", new Object());

        List<Object> failures = buildCheckSection(badChecks);
        List<Object> oks = buildCheckSection(goodChecks);

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

    private List<Object> buildCheckSection(List<CheckResult> checks){
        if(checks == null || checks.size() < 1)
            return null;

        List<Object> out = new ArrayList<>();
        for(CheckResult check : checks ){
            Map<String, Object> summaryPair = new HashMap<>();

            List<Map<String, Object>> assertList = new ArrayList<>();
            List<Map<String, Object>> summaryList = new ArrayList<>();
            Map<String, Object> testMap = new HashMap<>();
            Map<String, Object> summaryMap = new HashMap<>();

            assertList.add(createAssertRow(check));

            testMap.put("name", "name");
            testMap.put("asserts", assertList);

            summaryMap.put("details", testMap);

            summaryList.add(summaryMap);

            summaryPair.put("name", check.getName());
            summaryPair.put("value",summaryList);
            out.add(summaryPair);
        }
        return out;
    }

    private Map<String, Object> createAssertRow(CheckResult check) {
        Map<String, Object> assertMap = new HashMap<>();
        assertMap.put("name", check.getName());
        assertMap.put("comment",check.getDetails());
        assertMap.put("expected_result", "Passed");
        assertMap.put("output_value", check.isPassed() ? "Passed" : "Failed");
        return assertMap;
    }
}
