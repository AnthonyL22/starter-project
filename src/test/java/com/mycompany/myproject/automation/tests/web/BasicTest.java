package com.mycompany.myproject.automation.tests.web;

import com.mycompany.myproject.automation.data.Constants;
import com.mycompany.myproject.automation.frameworksupport.Groups;
import com.mycompany.myproject.automation.frameworksupport.MyApplicationTestCase;
import com.pwc.core.framework.annotations.Issue;
import com.pwc.core.framework.annotations.MaxRetryCount;
import com.pwc.core.framework.annotations.TestCase;
import com.pwc.core.framework.listeners.Retry;
import org.testng.annotations.Test;

import static com.pwc.logging.service.LoggerService.FEATURE;
import static com.pwc.logging.service.LoggerService.GIVEN;
import static com.pwc.logging.service.LoggerService.SCENARIO;
import static com.pwc.logging.service.LoggerService.THEN;
import static com.pwc.logging.service.LoggerService.WHEN;

public class BasicTest extends MyApplicationTestCase {

    @Override
    public void beforeMethod() {
    }

    @Override
    public void afterMethod() {
    }

    @TestCase("Zephyr-123")
    @Issue("STORY-777")
    @MaxRetryCount(3)
    @Test(retryAnalyzer = Retry.class, groups = {Groups.ACCEPTANCE_TEST})
    public void testBasic() {

        FEATURE("Web-Based Feature Under Test");
        SCENARIO("Scenario Being Tested Here");

        GIVEN("I have done something");
        webElementVisible(Constants.LOGO_ANCHOR_IMAGE);

        WHEN("I do something");
        redirect("/help");
        webAction(Constants.HELP_ANCHOR);

        THEN("Something happens as expected");
        webElementExists(Constants.HELP_ANCHOR);

    }

}
