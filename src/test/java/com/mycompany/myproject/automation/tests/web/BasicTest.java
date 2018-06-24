package com.mycompany.myproject.automation.tests.web;

import com.mycompany.myproject.automation.data.Constants;
import com.mycompany.myproject.automation.frameworksupport.Groups;
import com.mycompany.myproject.automation.frameworksupport.MyApplicationTestCase;
import com.pwc.core.framework.JavascriptConstants;
import com.pwc.core.framework.annotations.Issue;
import com.pwc.core.framework.annotations.MaxRetryCount;
import com.pwc.core.framework.listeners.Retry;
import org.testng.annotations.Test;

import static com.pwc.logging.service.LoggerService.FEATURE;
import static com.pwc.logging.service.LoggerService.GIVEN;
import static com.pwc.logging.service.LoggerService.SCENARIO;
import static com.pwc.logging.service.LoggerService.THEN;
import static com.pwc.logging.service.LoggerService.WHEN;

public class BasicTest extends MyApplicationTestCase {

    private static final String SEARCH_TEXT = "pacificwebconsulting";

    @Override
    public void beforeMethod() {
    }

    @Override
    public void afterMethod() {
    }

    @Issue("STORY-777")
    @MaxRetryCount(5)
    @Test(retryAnalyzer = Retry.class, groups = {Groups.ACCEPTANCE_TEST})
    public void testBasic() {

        FEATURE("Web-Based Feature Under Test");
        SCENARIO("Scenario Being Tested Here");

        GIVEN("I have done something");
        webElementVisible(Constants.LOGO_IMAGE);

        WHEN("I do something");
        webAction(Constants.QUERY_INPUT, SEARCH_TEXT);

        THEN("Something happens as expected");
        webAction(Constants.SEARCH_BUTTON);
        webElementExists(Constants.CORE_ANCHOR);

        webAction(Constants.RUNNER_ANCHOR);
        webElementTextEquals(Constants.QUERY_INPUT, "a:\"runner-microservice\"");

        webAction(Constants.ADVANCED_SEARCH_ANCHOR);
        webElementTextEquals(Constants.PACKAGING_INPUT, "");

        executeJavascript(JavascriptConstants.CLICK_BY_XPATH, Constants.ADVANCED_SEARCH_ANCHOR);
        webElementTextEquals(Constants.PACKAGING_INPUT, "");

    }

}
