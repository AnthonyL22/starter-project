package com.mycompany.myproject.automation.tests.headless;

import com.jayway.restassured.path.json.JsonPath;
import com.mycompany.myproject.automation.data.Data;
import com.mycompany.myproject.automation.frameworksupport.Groups;
import com.mycompany.myproject.automation.frameworksupport.ReportableTestCase;
import com.pwc.core.framework.FrameworkConstants;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicStatusLine;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Set;

import static com.pwc.logging.service.LoggerService.*;

public class SitemapTest extends ReportableTestCase {

    private static final String OUTPUT_FILE_NAME = "SitemapTest.csv";

    @BeforeTest(alwaysRun = true)
    public void beforeTest() {
        setHeadlessMode(true);
    }

    @Override
    public void beforeMethod() {
    }

    @Override
    public void afterMethod() {
    }

    @Test(groups = {Groups.ACCEPTANCE_TEST})
    public void testSitemap() {

        FEATURE("Sitemap Checking");
        SCENARIO("Validate all page sitemap.xml URLs");

        GIVEN("I am able to access each URL and store all failures for endpoints");
        File file = new File(OUTPUT_FILE_NAME);
        if (file.exists()) {
            FileUtils.deleteQuietly(file);
        }

        writeHeaderToLogFile(Arrays.asList("PAGE_STATUS_CODE", "URL"), file);

        int count = 0;
        Set<String> allURLs = getURLsFromSitemap(Data.EXAMPLE_SITEMAP_URL);
        for (String location : allURLs) {

            LOG(true, "Analyzing %s of %s pages", count, allURLs.size());
            JsonPath response = (JsonPath) httpAction(null, location.trim());
            StatusLine status = new BasicStatusLine(HttpVersion.HTTP_1_1, response.get(FrameworkConstants.HTTP_STATUS_VALUE_KEY), response.get(FrameworkConstants.HTTP_STATUS_REASON_PHRASE_KEY));
            if (status.getStatusCode() != HttpStatus.SC_OK) {
                writeToLogFile(file, String.valueOf(status.getStatusCode()), location);
            }

            count++;

        }

    }

}
