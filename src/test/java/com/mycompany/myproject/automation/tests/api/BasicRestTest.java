package com.mycompany.myproject.automation.tests.api;

import com.jayway.restassured.path.json.JsonPath;
import com.mycompany.myproject.automation.data.Constants;
import com.mycompany.myproject.automation.frameworksupport.Groups;
import com.mycompany.myproject.automation.frameworksupport.MyApplicationTestCase;
import com.mycompany.myproject.automation.frameworksupport.command.webservice.WebServiceCommand;
import com.pwc.core.framework.FrameworkConstants;
import com.pwc.core.framework.annotations.MaxRetryCount;
import com.pwc.core.framework.listeners.Retry;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.pwc.assertion.AssertService.assertEquals;
import static com.pwc.assertion.AssertService.assertGreaterThan;
import static com.pwc.assertion.AssertService.assertLessThanOrEqual;
import static com.pwc.logging.service.LoggerService.FEATURE;
import static com.pwc.logging.service.LoggerService.GIVEN;
import static com.pwc.logging.service.LoggerService.SCENARIO;
import static com.pwc.logging.service.LoggerService.THEN;
import static com.pwc.logging.service.LoggerService.WHEN;

public class BasicRestTest extends MyApplicationTestCase {

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

    @MaxRetryCount(1)
    @Test(retryAnalyzer = Retry.class, groups = {Groups.ACCEPTANCE_TEST})
    public void testBasicRest() {

        FEATURE("Webservice-Based Feature Under Test");
        SCENARIO("Scenario Being Tested Here");

        GIVEN("I can query a public REST service");
        JsonPath response = (JsonPath) webServiceAction(WebServiceCommand.GET_LIST_ALL_BREEDS);

        WHEN("I verify the REST results");
        JsonPath entity = new JsonPath(response.get(FrameworkConstants.HTTP_ENTITY_KEY).toString());
        HashMap dogBreeds = entity.get("message");

        THEN("The REST service responds correctly");
        assertGreaterThan("All Dog Breeds are returned", dogBreeds.size(), 0);
        assertEquals("Verify Http Status Value", response.getInt(FrameworkConstants.HTTP_STATUS_VALUE_KEY), org.apache.http.HttpStatus.SC_OK);
        assertLessThanOrEqual("Verify WS Performance", response.get(FrameworkConstants.HTTP_RESPONSE_TIME_KEY), Constants.MAX_WEB_SERVICE_RESPONSE_TIME);

    }

}
