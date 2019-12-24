package com.mycompany.myproject.automation.tests.manual;

import com.mycompany.myproject.automation.frameworksupport.Groups;
import com.mycompany.myproject.automation.frameworksupport.MyApplicationTestCase;
import com.pwc.core.framework.annotations.Issue;
import com.pwc.core.framework.annotations.MaxRetryCount;
import com.pwc.core.framework.annotations.TestCase;
import com.pwc.core.framework.listeners.Retry;
import org.testng.annotations.Test;

import static com.pwc.logging.service.LoggerService.AND;
import static com.pwc.logging.service.LoggerService.BUT;
import static com.pwc.logging.service.LoggerService.FEATURE;
import static com.pwc.logging.service.LoggerService.FINALLY;
import static com.pwc.logging.service.LoggerService.GIVEN;
import static com.pwc.logging.service.LoggerService.IF;
import static com.pwc.logging.service.LoggerService.NOT;
import static com.pwc.logging.service.LoggerService.OR;
import static com.pwc.logging.service.LoggerService.SCENARIO;
import static com.pwc.logging.service.LoggerService.THEN;
import static com.pwc.logging.service.LoggerService.WHEN;

public class RemoveUserManualTest extends MyApplicationTestCase {

    @Override
    public void beforeMethod() {
    }

    @Override
    public void afterMethod() {
    }

    @TestCase("STORY-12234")
    @Issue("STORY-777")
    @MaxRetryCount(3)
    @Test(retryAnalyzer = Retry.class, groups = {Groups.ACCEPTANCE_TEST})
    public void testRemoveUserManual() {

        FEATURE("Remove User Test");
        SCENARIO("Basic Remove Functionality");
        GIVEN("I am logged in page=%s and authenticated user=%s", "home", "Home");

        WHEN("I view the Landing page without doing a search for env=%s", "TEST");

        THEN("Basic remove components are present in body of message=%s", "Hello World");

        AND("I can click the DELETE button");

        BUT("I go back to the Remove User page");

        OR("I not am blocked by a popup");

        IF("I can click the HOME");

    }

}
