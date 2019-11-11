package com.mycompany.myproject.automation.tests.headless;

import com.jayway.restassured.path.json.JsonPath;
import com.mycompany.myproject.automation.data.Data;
import com.mycompany.myproject.automation.frameworksupport.Groups;
import com.mycompany.myproject.automation.frameworksupport.ReportableTestCase;
import com.pwc.core.framework.FrameworkConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicStatusLine;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.pwc.logging.service.LoggerService.FEATURE;
import static com.pwc.logging.service.LoggerService.GIVEN;
import static com.pwc.logging.service.LoggerService.LOG;
import static com.pwc.logging.service.LoggerService.SCENARIO;

public class SitemapAllHrefValidationTest extends ReportableTestCase {

    private static final String OUTPUT_FILE_NAME = "SitemapAllHrefValidationTest.csv";

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
    public void testSitemapAllHrefValidation() {

        FEATURE("Sitemap Checking");
        SCENARIO("Validate every HREF for every page in the sitemap.xml");

        GIVEN("I am able to access each URL and store all failures for endpoints");
        File file = new File(OUTPUT_FILE_NAME);
        if (file.exists()) {
            FileUtils.deleteQuietly(file);
        }

        writeHeaderToLogFile(Arrays.asList("PAGE_STATUS_CODE", "URL_CURRENTLY_ON", "URL_LINKED_TO"), file);

        int count = 0;
        Set<String> uniqueUrls = getURLsFromSitemap(Data.EXAMPLE_SITEMAP_URL);
        for (String location : uniqueUrls) {

            LOG(true, "Analyzing %s of %s pages", count, uniqueUrls.size());
            String pageHtml = htmlAction(null, location);

            Pattern p = Pattern.compile("href=\"(.*?)\"", Pattern.DOTALL);
            Matcher m = p.matcher(pageHtml);
            while (m.find()) {
                String href = m.group(1);
                if (!StringUtils.containsIgnoreCase(href, ".com")) {
                    href = StringUtils.substringBefore(Data.EXAMPLE_SITEMAP_URL, ".com") + ".com" + href;
                }
                JsonPath response = (JsonPath) httpAction(null, href);
                StatusLine status = new BasicStatusLine(HttpVersion.HTTP_1_1, response.get(FrameworkConstants.HTTP_STATUS_VALUE_KEY), response.get(FrameworkConstants.HTTP_STATUS_REASON_PHRASE_KEY));
                if (status.getStatusCode() != HttpStatus.SC_OK) {
                    if (StringUtils.contains(href, " ")) {
                        writeToLogFile(file, "SPACES", href, location);
                    } else {
                        writeToLogFile(file, String.valueOf(status.getStatusCode()), href, location);
                    }
                }
            }

            count++;

        }

    }

}
