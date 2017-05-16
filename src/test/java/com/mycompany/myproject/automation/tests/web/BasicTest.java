package com.mycompany.myproject.automation.tests.web;

import com.mycompany.myproject.automation.data.Constants;
import com.mycompany.myproject.automation.frameworksupport.Groups;
import com.mycompany.myproject.automation.frameworksupport.MyApplicationTestCase;
import com.pwc.core.framework.annotations.MaxRetryCount;
import com.pwc.core.framework.listeners.Retry;
import org.testng.annotations.Test;

import static com.pwc.logging.service.LoggerService.*;

public class BasicTest extends MyApplicationTestCase {

    public static final String SEARCH_TEXT = "pacificwebconsulting";

    @Override
    public void beforeMethod() {
    }

    @Override
    public void afterMethod() {
    }

    @MaxRetryCount(1)
    @Test(retryAnalyzer = Retry.class, groups = {Groups.ACCEPTANCE_TEST})
    public void testBasic() {

        FEATURE("Feature Under Test");
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

    }

}
