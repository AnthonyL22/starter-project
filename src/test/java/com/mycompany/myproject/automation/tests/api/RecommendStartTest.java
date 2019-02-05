package com.mycompany.myproject.automation.tests.api;

import com.jayway.restassured.path.json.JsonPath;
import com.mycompany.myproject.automation.data.Constants;
import com.mycompany.myproject.automation.frameworksupport.Groups;
import com.mycompany.myproject.automation.frameworksupport.MyApplicationTestCase;
import com.mycompany.myproject.automation.frameworksupport.command.webservice.TaylorMadeCommand;
import com.pwc.core.framework.FrameworkConstants;
import com.pwc.core.framework.listeners.Retry;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.pwc.assertion.AssertService.assertEquals;
import static com.pwc.assertion.AssertService.assertLessThanOrEqual;
import static com.pwc.logging.service.LoggerService.*;

public class RecommendStartTest extends MyApplicationTestCase {

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

    @Test(retryAnalyzer = Retry.class, groups = {Groups.ACCEPTANCE_TEST})
    public void testGetProductAvailability() {

        FEATURE("Product Availability");
        SCENARIO("Service to get availability by PID value");

        GIVEN("I can query a public REST service");
        JsonPath response = (JsonPath) webServiceAction(TaylorMadeCommand.POST_RECOMMENDED_START);

        WHEN("I verify the REST results");
        JsonPath entity = new JsonPath(response.get(FrameworkConstants.HTTP_ENTITY_KEY).toString());

        THEN("The REST service responds correctly");
        assertEquals("Verify Http Status Value", response.getInt(FrameworkConstants.HTTP_STATUS_VALUE_KEY), org.apache.http.HttpStatus.SC_OK);
        assertLessThanOrEqual("Verify WS Performance", response.get(FrameworkConstants.HTTP_RESPONSE_TIME_KEY), Constants.MAX_WEB_SERVICE_RESPONSE_TIME);

    }

}
