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

import java.util.HashMap;
import java.util.Map;

import static com.pwc.assertion.AssertService.*;
import static com.pwc.logging.service.LoggerService.*;

public class GetProductAvailabilityTest extends MyApplicationTestCase {

    private static final String PID_TO_FIND = "B1079901";

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

        Map map = new HashMap<>();
        map.put("pid", PID_TO_FIND);
        map.put("Quantity", "1");

        GIVEN("I can query a public REST service");
        JsonPath response = (JsonPath) webServiceAction(TaylorMadeCommand.GET_PRODUCT_AVAILABILITY, map);

        WHEN("I verify the REST results");
        JsonPath entity = new JsonPath(response.get(FrameworkConstants.HTTP_ENTITY_KEY).toString());

        THEN("The REST service responds correctly");
        assertEquals("Verify Http Status Value", response.getInt(FrameworkConstants.HTTP_STATUS_VALUE_KEY), org.apache.http.HttpStatus.SC_OK);
        assertLessThanOrEqual("Verify WS Performance", response.get(FrameworkConstants.HTTP_RESPONSE_TIME_KEY), Constants.MAX_WEB_SERVICE_RESPONSE_TIME);

        assertNotNull(entity.get("availableForSale"));
        assertNotNull(entity.get("ATS"));
        assertNotNull(entity.get("isAvailable"));

    }

}
