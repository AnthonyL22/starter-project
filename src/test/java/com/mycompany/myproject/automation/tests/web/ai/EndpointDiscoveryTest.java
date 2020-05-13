package com.mycompany.myproject.automation.tests.web.ai;

import com.mycompany.myproject.automation.data.Constants;
import com.mycompany.myproject.automation.frameworksupport.Groups;
import com.mycompany.myproject.automation.frameworksupport.MachineLearningTestCase;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static com.pwc.logging.service.LoggerService.AND;
import static com.pwc.logging.service.LoggerService.FEATURE;
import static com.pwc.logging.service.LoggerService.GIVEN;
import static com.pwc.logging.service.LoggerService.SCENARIO;
import static com.pwc.logging.service.LoggerService.THEN;
import static com.pwc.logging.service.LoggerService.WHEN;

public class EndpointDiscoveryTest extends MachineLearningTestCase {

    private static final String SHORT_TERM_COGNITIVE_MEMORY = "memory.json";
    private static final File STORAGE_FILE = new File(SHORT_TERM_COGNITIVE_MEMORY);
    private boolean memorize = false;
    private Set<String> base = new HashSet<>();

    @Override
    public void beforeMethod() {
        base = readEndpointsFromMemory(memorize, STORAGE_FILE, base);
    }

    @Override
    public void afterMethod() {
        storeEndpointsToMemory(memorize, STORAGE_FILE, base);
    }

    @Test(groups = {Groups.MACHINE_LEARNING_TEST})
    public void testEndpointDiscovery() {

        FEATURE("Discovery Test");
        SCENARIO("Randomized Test of all @HREF Anchor Tags a web application");

        GIVEN("I am an authenticated user");
        webElementExists(Constants.LOGO_ANCHOR_IMAGE);

        WHEN("I get all the HREF links from the landing page");
        Set<String> originalDiscoveredLinks = collectVisibleHrefLinks();
        base.addAll(originalDiscoveredLinks);

        AND("The landing page's children's links - Level 2 (go as deep as you want in the web app)");
        Set<String> level2 = mineDataFromLikeEndpoints(base);
        base.addAll(level2);

        THEN("Validate all discovered endpoints are available and no Console errors are occurred");
        verifyEndpointSet(base);

    }

}
