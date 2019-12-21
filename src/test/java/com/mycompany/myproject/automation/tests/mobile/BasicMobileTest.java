package com.mycompany.myproject.automation.tests.mobile;

import com.mycompany.myproject.automation.frameworksupport.Groups;
import com.pwc.core.framework.MobileTestCase;
import com.pwc.core.framework.annotations.Issue;
import com.pwc.core.framework.annotations.MaxRetryCount;
import com.pwc.core.framework.listeners.Retry;
import org.testng.annotations.Test;

public class BasicMobileTest extends MobileTestCase {

    @Override
    public void beforeMethod() {
    }

    @Override
    public void afterMethod() {
    }

    @Issue("STORY-777")
    @MaxRetryCount(1)
    @Test(retryAnalyzer = Retry.class, groups = {Groups.ACCEPTANCE_TEST})
    public void testBasicMobile() {

        mobileAction("General");
        mobileAction("XCUIElementTypeApplication");
        mobileAction("type == 'XCUIElementTypeStaticText' and name == 'General'");
        mobileAction("type == 'XCUIElementTypeApplication' and name == 'Settings'");

        resetApp();


    }

}
