package com.mycompany.myproject.automation.tests.web;

import com.mycompany.myproject.automation.frameworksupport.Groups;
import com.mycompany.myproject.automation.frameworksupport.MyApplicationTestCase;
import com.pwc.core.framework.annotations.Issue;
import com.pwc.core.framework.annotations.MaxRetryCount;
import com.pwc.core.framework.listeners.Retry;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class XPathTest extends MyApplicationTestCase {

    @Override
    public void beforeMethod() {
    }

    @Override
    public void afterMethod() {
    }

    @Issue("STORY-777")
    @MaxRetryCount(5)
    @Test(retryAnalyzer = Retry.class, groups = {Groups.ACCEPTANCE_TEST})
    public void testXPath() {

        // Test Bed
        //http://www.whitebeam.org/library/guide/TechNotes/xpathtestbed.rhtm

        //Showing First item Selenium & xPath finds
        webEventController.getWebEventService().findWebElement("//a[contains(@href, '#')]").click();

        // Show sibling Element before
        webEventController.getWebEventService().findWebElement("//input[@id='query']/preceding-sibling::label").getAttribute("for");

        // Show sibling Element after
        webEventController.getWebEventService().findWebElement("//input[@id='query']/following-sibling::label").getAttribute("for");

        // ONLY VISIBLE for entire page
        webEventController.getWebEventService().findWebElement("//div[@id='toppanel']");
        webEventController.getWebEventService().findWebElement("//div[@id='toppanel']").findElements(By.xpath("//a[not(contains(@style,'display:none'))]"));
        webEventController.getWebEventService().findWebElement("//div[@id='toppanel']").findElements(By.xpath("//a[not(contains(@style,'display:none'))]")).get(10).getAttribute("text");


    }

}
