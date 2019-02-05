package com.mycompany.myproject.automation.tests.web.deep;

import com.mycompany.myproject.automation.data.Constants;
import com.mycompany.myproject.automation.frameworksupport.Groups;
import com.mycompany.myproject.automation.frameworksupport.MyApplicationTestCase;
import com.pwc.core.framework.annotations.Issue;
import com.pwc.core.framework.annotations.MaxRetryCount;
import com.pwc.core.framework.listeners.Retry;
import org.testng.annotations.Test;

import java.util.logging.Level;

import static com.pwc.logging.service.LoggerService.*;

public class BrowserDiagnosticsTest extends MyApplicationTestCase {

    public static final String SEARCH_TEXT = "pacificwebconsulting";

    @Override
    public void beforeMethod() {
    }

    @Override
    public void afterMethod() {
    }

    @Issue("STORY-1234")
    @MaxRetryCount(1)
    @Test(retryAnalyzer = Retry.class, groups = {Groups.ACCEPTANCE_TEST})
    public void testBrowserDiagnostics() {

        FEATURE("Web Diagnostics");
        SCENARIO("Scenario Being Tested Here");

        GIVEN("I have done something");
        webDiagnosticsConsoleContains("loaded over HTTPS");

        WHEN("I do something");
        webAction(Constants.KEYWORD_INPUT, SEARCH_TEXT);

        THEN("Something happens as expected");
        webAction(Constants.SEARCH_INPUT);
        webDiagnosticsConsoleLevelBelow(Level.WARNING);
        webDiagnosticsRequestCountEquals("https://search.maven.org/solrsearch", 1);

    }

}
