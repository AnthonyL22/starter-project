package com.mycompany.myproject.automation.tests.web;

import com.mycompany.myproject.automation.data.Constants;
import com.mycompany.myproject.automation.frameworksupport.Groups;
import com.mycompany.myproject.automation.frameworksupport.MyApplicationTestCase;
import com.pwc.core.framework.annotations.Issue;
import com.pwc.core.framework.annotations.MaxRetryCount;
import com.pwc.core.framework.listeners.Retry;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import static com.pwc.logging.service.LoggerService.FEATURE;
import static com.pwc.logging.service.LoggerService.GIVEN;
import static com.pwc.logging.service.LoggerService.SCENARIO;
import static com.pwc.logging.service.LoggerService.THEN;
import static com.pwc.logging.service.LoggerService.WHEN;

public class BasicTest extends MyApplicationTestCase {

    private static final String SEARCH_TEXT = "automated test engineer";

    @Override
    public void beforeMethod() {
    }

    @Override
    public void afterMethod() {
    }

    @Issue("STORY-777")
    @MaxRetryCount(3)
    @Test(retryAnalyzer = Retry.class, groups = {Groups.ACCEPTANCE_TEST})
    public void testBasic() {

        FEATURE("Web-Based Feature Under Test");
        SCENARIO("Scenario Being Tested Here");

        GIVEN("I have done something");
        webElementVisible(Constants.LOGO_ANCHOR);

        Set<String> consoleIgnoreSet = new HashSet<>();
        consoleIgnoreSet.add("masymiser");
        consoleIgnoreSet.add("adobe");
        consoleIgnoreSet.add(".js");
        webDiagnosticsConsoleRequestGreaterThanOrEqual(Constants.LOGO_ANCHOR, Level.SEVERE, consoleIgnoreSet);

        WHEN("I do something");
        redirect("/corporate/careers");
        webAction(Constants.JOB_SEARCH_ANCHOR);
        webAction(Constants.KEYWORD_INPUT, SEARCH_TEXT);

        THEN("Something happens as expected");
        webAction(Constants.SEARCH_INPUT);
        webElementExists(Constants.APPLY_ANCHOR);

    }

}
