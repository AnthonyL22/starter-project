package com.mycompany.myproject.automation.tests.headless;

import com.mycompany.myproject.automation.data.Data;
import com.mycompany.myproject.automation.frameworksupport.Groups;
import com.mycompany.myproject.automation.frameworksupport.ReportableTestCase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Set;

import static com.pwc.logging.service.LoggerService.*;

public class CompanyNameSpellingTest extends ReportableTestCase {

    private static final String OUTPUT_FILE_NAME = "CompanyNameSpellingTest.csv";

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
    public void testCompanyNameSpelling() {

        FEATURE("Sitemap Checking");
        SCENARIO("Validate the Company Name is spelled according to corporate standards on all pages");

        GIVEN("I am able to access each URL and store all failures for endpoints");
        File file = new File(OUTPUT_FILE_NAME);
        if (file.exists()) {
            FileUtils.deleteQuietly(file);
        }

        writeHeaderToLogFile(Arrays.asList("MISSPELLING", "URL"), file);

        int count = 0;
        Set<String> allURLs = getURLsFromSitemap(Data.EXAMPLE_SITEMAP_URL);
        for (String location : allURLs) {

            LOG(true, "Analyzing %s of %s pages", count, allURLs.size());
            String rawHtml = htmlAction(null, location);
            String htmlText = getAllTextFromRawHtml(rawHtml);

            if (StringUtils.contains(htmlText, "SouthWest")) {
                writeToLogFile(file, "SouthWest", location);
            } else if (StringUtils.contains(htmlText, "southwest")) {
                writeToLogFile(file, "southwest", location);
            }

            count++;

        }

    }

}
