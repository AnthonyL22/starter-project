package com.mycompany.myproject.automation.frameworksupport;

import com.pwc.core.framework.data.WebElementAttribute;
import com.pwc.core.framework.data.WebElementType;
import com.pwc.core.framework.util.FileUtils;
import com.pwc.core.framework.util.JsonUtils;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import static com.pwc.logging.service.LoggerService.LOG;

public abstract class MachineLearningTestCase extends MyApplicationTestCase {

    /**
     * Redirect, wait for page to load, and collect all visible @href WebElements.
     */
    protected Set<String> mineDataFromLikeEndpoints(Set<String> base) {

        Set<String> current = new HashSet<>();
        for (String href : base) {
            if (StringUtils.isNotEmpty(href) && !StringUtils.contains(href, "mailto")) {
                if (href.contains(" ")) {
                    redirect(StringUtils.replace(href, " ", "%20"));
                } else {
                    redirect(href);
                }
                current.addAll(collectVisibleHrefLinks());
            }
        }
        return current;
    }

    /**
     * Find and store all visible WebElements on a page that contain an @href attribute.
     */
    protected Set<String> collectVisibleHrefLinks() {

        Set<String> current = new HashSet<>();
        List<WebElement> anchorElements = webEventController.getWebEventService().getMicroserviceWebDriver().findElementsByTagName(WebElementType.ANCHOR.type);
        for (WebElement link : anchorElements) {
            try {
                if (link.isDisplayed()) {
                    current.add(link.getAttribute(WebElementAttribute.HREF.attribute));
                }
            } catch (Exception e) {
                LOG(true, "Message='%s'", e);
            }
        }
        return scrubEndpointSet(current);
    }

    /**
     * Verify the @href endpoint returns a 200 response code.
     *
     * @param endpointHrefSet Set of stored href attributes from previous mining operations
     */
    protected void verifyEndpointSet(Set<String> endpointHrefSet) {

        LOG(true, "Checking %s Number of URLs", endpointHrefSet.size());
        for (String href : endpointHrefSet) {
            if (StringUtils.isNotEmpty(href) && !StringUtils.contains(href, "mailto")) {
                LOG(true, "Redirect to page=%s", href);
                executeJavascript(String.format("window.location.replace('%s')", href));
                waitForBrowserToLoad();
                webDiagnosticsConsoleLevelBelow(Level.WARNING);
            }
        }
    }

    /**
     * Read the last time endpoints were mined from the application as a starting point.
     *
     * @param memorize memorize flag
     */
    protected Set<String> readEndpointsFromMemory(final boolean memorize, File jsonStorageFile, Set<String> base) {

        if (memorize) {
            LOG(true, "Read endpoints from JSON memory file");
            List<String> priorTestData = FileUtils.readFile(jsonStorageFile, jsonStorageFile.getName());
            if (!priorTestData.isEmpty()) {
                Set<String> current = new HashSet<>();
                String[] x = org.apache.commons.lang3.StringUtils.split(priorTestData.get(0), ",");
                for (String s : x) {
                    String scrubbed = org.apache.commons.lang3.StringUtils.remove(s, "]");
                    current.add(scrubbed);
                }
                base.addAll(current);
            }
        }
        return scrubEndpointSet(base);
    }

    /**
     * Save current runs endpoints to memory (JSON file).
     */
    protected void storeEndpointsToMemory(final boolean memorize, File jsonStorageFile, Set<String> base) {

        if (memorize) {
            LOG(true, "Store endpoints used during last test");
            scrubEndpointSet(base);
            String endpointsStorage = JsonUtils.getJSONString(base);
            FileUtils.createFile(jsonStorageFile, jsonStorageFile.getName(), endpointsStorage);
        }
    }

    /**
     * Scrub Set of all bad endpoints based on the original URL used to access the AUT.
     *
     * @param setToScrub data set to scrub
     * @return cleaned Set of endpoints related to original URL
     */
    private Set<String> scrubEndpointSet(Set<String> setToScrub) {

        Set<String> scrubbed = new HashSet<>();
        for (String endpointToScrub : setToScrub) {
            if (StringUtils.isNotEmpty(endpointToScrub)) {
                if (StringUtils.containsIgnoreCase(endpointToScrub, webEventController.getWebEventService().getUrl())) {
                    scrubbed.add(endpointToScrub);
                }
            }
        }
        return scrubbed;
    }

}
