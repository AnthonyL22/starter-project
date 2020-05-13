package com.mycompany.myproject.automation.frameworksupport;

import com.jayway.restassured.path.json.JsonPath;
import com.mycompany.myproject.automation.data.Constants;
import com.mycompany.myproject.automation.data.enums.TestingUserQueue;
import com.mycompany.myproject.automation.frameworksupport.type.Account;
import com.pwc.core.framework.FrameworkConstants;
import com.pwc.core.framework.WebTestCase;
import com.pwc.core.framework.command.WebServiceCommand;
import com.pwc.core.framework.util.FileUtils;
import com.pwc.core.framework.util.PropertiesUtils;
import com.pwc.logging.helper.LoggerHelper;
import com.pwc.logging.service.VideoLoggerService;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Jsoup;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import static com.pwc.assertion.AssertService.assertEquals;
import static com.pwc.assertion.AssertService.assertFail;
import static com.pwc.logging.service.LoggerService.LOG;

public abstract class MyApplicationTestCase extends WebTestCase {

    private Account currentUser;
    private boolean headlessMode = false;

    @BeforeClass(alwaysRun = true)
    public void setUp() {

        if (null == currentUser) {
            currentUser = TestingUserQueue.getNextAvailableUser();
        }

        if (!isHeadlessMode()) {
            webAction(Constants.LOGO_ANCHOR_IMAGE);
        }

    }

    @BeforeMethod(firstTimeOnly = true)
    public void preserveProduction(Method m) {

        if (StringUtils.containsIgnoreCase(System.getProperty(FrameworkConstants.AUTOMATION_TEST_ENVIRONMENT), "prod")) {
            List allowedProductionGroups = new ArrayList();
            allowedProductionGroups.add(Groups.ACCEPTANCE_TEST);

            Test currentTest = m.getAnnotation(Test.class);
            if (!allowedProductionGroups.containsAll(Arrays.asList(currentTest.groups()))) {
                assertFail("PREVENTING EXECUTION OF DESTRUCTIVE TEST='%s' IN PRODUCTION", m.getName());
                tearDownClass();
                System.exit(1);
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {

        Properties properties = PropertiesUtils.getPropertiesFromPropertyFile("config/" + System.getProperty(FrameworkConstants.AUTOMATION_TEST_ENVIRONMENT) + "/automation.properties");
        if (Boolean.valueOf(properties.get("capture.video").toString())) {
            String testName = LoggerHelper.getClassName(Reporter.getCurrentTestResult());
            URL sourceFilesDirectory = getClass().getClassLoader().getResource("screenshots/" + testName);

            VideoLoggerService videoLoggerService = new VideoLoggerService();
            videoLoggerService.setWidth(500);
            videoLoggerService.setHeight(500);
            videoLoggerService.setFrameRate(2);
            videoLoggerService.setSourceFilesDirectoryURL(sourceFilesDirectory.getPath());
            videoLoggerService.setOutputMovieFileName(testName + ".mov");
            videoLoggerService.convert();
        }
    }

    /**
     * Send a REST ws action to a service End Point.
     *
     * @param command BaseGetCommand command type
     */
    protected Object webServiceAction(final WebServiceCommand command) {
        return super.webServiceAction(command, null);
    }

    /**
     * Send a REST ws action to a service End Point.
     *
     * @param command     BaseGetCommand command type
     * @param requestBody POST request body
     */
    protected Object webServiceAction(final WebServiceCommand command, final Object requestBody) {
        if (requestBody instanceof HashMap || requestBody instanceof List) {
            return super.webServiceAction(command, null, requestBody);
        } else {
            return super.webServiceAction(command, requestBody, null);
        }
    }

    /**
     * Send a REST ws action to a service End Point.
     *
     * @param command      BaseGetCommand command type
     * @param parameterMap Name-Value pair filled map of parameters to send in HTTP request
     */
    protected Object webServiceAction(final WebServiceCommand command, final HashMap<String, Object> parameterMap) {
        return super.webServiceAction(command, null, parameterMap);
    }

    /**
     * Get all URLs from a given sitemap file.
     *
     * @param siteMapUrl sitemap.xml url
     * @return complete set of URL's from given sitemap
     */
    protected Set<String> getURLsFromSitemap(final String siteMapUrl) {

        Set<String> urlSet = new HashSet<>();
        JsonPath jsonObject = convertXmlToJson(siteMapUrl);
        HashMap urlSetMap = jsonObject.get("urlset");
        List<HashMap> allURLs = (List) urlSetMap.get("url");
        allURLs.forEach(eachUrl -> {
            urlSet.add((String) eachUrl.get("loc"));
        });
        return urlSet;
    }

    /**
     * Extract all text from the HTML page.
     *
     * @param rawHtml raw HTML of a page
     * @return all non-html strings found in the page
     */
    protected String getAllTextFromRawHtml(final String rawHtml) {

        String textOnly = "";
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new StringReader(rawHtml));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            textOnly = Jsoup.parse(sb.toString()).text();
            return textOnly;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textOnly;
    }

    /**
     * Convert XML to JSON from driver version APIs.
     *
     * @return JSON representation of XML from WEB api
     */
    private static JsonPath convertXmlToJson(final String baseUrl) {

        JsonPath jsonObject = null;

        try {

            URL target = new URL(baseUrl);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(target.openStream());

            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);

            JSONObject json = XML.toJSONObject(writer.toString());
            jsonObject = new JsonPath(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;

    }

    /**
     * Get a random snippet of a given String from the 0 index to a random location in the given <code>String</code>.
     *
     * @param displayedColumnValue base value to get a snippet of
     * @return sub-string of original value
     */
    protected String getRandomBeginningSubString(String displayedColumnValue) {
        int randomEnd = 1 + (int) (Math.random() * displayedColumnValue.length() - 1);
        return StringUtils.substring(displayedColumnValue, 0, randomEnd);
    }

    /**
     * Get a random fragment of a given sentence of words.
     *
     * @param displayedColumnValue base value to get a snippet or sentence
     * @return sub-string of original value
     */
    protected String getRandomSubSentenceFragment(String displayedColumnValue) {

        List<String> splitResult = new ArrayList<>(Arrays.asList(StringUtils.split(displayedColumnValue)));
        Random random = new Random();
        int randomEnd = 1 + (int) (random.nextDouble() * splitResult.size() - 1);

        String result = splitResult.get(0);
        for (int i = 1; i < randomEnd; i++) {
            result = result + " " + splitResult.get(i);
        }
        return result;
    }

    /**
     * Get a random snippet of a given String from a random location in the given <code>String</code> to the end.
     *
     * @param displayedColumnValue base value to get a snippet of
     * @return sub-string of original value
     */
    protected String getRandomEndingSubString(String displayedColumnValue) {
        Random random = new Random();
        int randomBegin = 1 + (int) (random.nextDouble() * displayedColumnValue.length() - 1);
        return StringUtils.substring(displayedColumnValue, randomBegin, displayedColumnValue.length());
    }

    /**
     * Get a random sub-list of a given values from a random set in the given <code>List</code> to the end.
     *
     * @param listValues base value to get a snippet of
     * @return sub-string of original value
     */
    protected List getRandomList(List listValues) {
        try {
            Random random = new Random();
            int randomBegin = 1 + (int) (random.nextDouble() * listValues.size() - 1);
            if ((listValues.subList(0, randomBegin)).isEmpty()) {
                return listValues;
            } else {
                return listValues.subList(0, randomBegin);
            }
        } catch (Exception e) {
            LOG(true, "Unable to get random list due to reason='%s'", e);
            return listValues;
        }
    }

    /**
     * Verify if the Console contains entries greater than or equal to the allowable Level.  This is a filtered list
     * for this specific project.
     *
     * @param elementIdentifier WebElement to wait for to display before reading Console tab data
     * @param filterLevel       {@link Level} the level to filter the log entries
     */
    protected void verifyConsoleRequests(final String elementIdentifier, final Level filterLevel) {

        LogEntries consoleEntries = webDiagnosticsConsoleRequests(elementIdentifier, filterLevel);
        long numberOfConsoleErrors = consoleEntries.spliterator().getExactSizeIfKnown() < 0 ? 0 : consoleEntries.spliterator().getExactSizeIfKnown();
        assertEquals("Console contains %s entries at Log Level = %s", numberOfConsoleErrors, 0L, numberOfConsoleErrors, filterLevel.getName());
    }

    /**
     * Harvest filtered list of Console requests.
     *
     * @param elementIdentifier WebElement to wait for to display before reading Console tab data
     * @param level             {@link Level} the level to filter the log entries
     * @return List of unique Console and Network requests
     */
    private LogEntries webDiagnosticsConsoleRequests(final String elementIdentifier, final Level level) {

        waitForBrowserToLoad();
        waitForElementToDisplay(elementIdentifier);

        Set<String> ignoreSet = new HashSet<>();
        ignoreSet.add("ignore_me");

        List<LogEntry> allRequests = super.webDiagnosticsConsoleRequests();
        ignoreSet.forEach(ignoreIt -> allRequests.removeIf(request -> request.getMessage().contains(ignoreIt)));

        LogEntries logEntries = new LogEntries(allRequests);
        logEntries.filter(level);
        return logEntries;
    }

    /**
     * Harvest filtered list of Network tab requests.
     *
     * @param elementIdentifier WebElement to wait for to display before reading Network tab data
     * @return List of unique Network requests
     */
    protected List<String> webDiagnosticsNetworkRequests(final String elementIdentifier) {

        waitForBrowserToLoad();
        waitForElementToDisplay(elementIdentifier);

        Set<String> ignoreSet = new HashSet<>();
        ignoreSet.add(".js");
        ignoreSet.add(".jpg");
        ignoreSet.add(".svg");

        List<String> allRequests = super.webNetworkRequests();
        ignoreSet.forEach(ignoreIt -> allRequests.removeIf(request -> request.contains(ignoreIt)));
        return allRequests;
    }

    /**
     * Save a performance statistic to file for later reading by an external user / Jenkins.
     *
     * @param fileName  file to write to
     * @param statistic statistic to store to file
     */
    protected void saveStatistics(String fileName, Object statistic) {

        fileName = Constants.LOAD_TEST_RESULTS_DIRECTORY + fileName;
        File stats = PropertiesUtils.getFileFromResources(fileName);
        if (stats == null) {
            FileUtils.createFile(fileName);
        }

        if (statistic instanceof Integer) {
            statistic = Integer.parseInt(statistic.toString()) * 1000;
            statistic = Float.valueOf(statistic.toString()) / 1000;
        } else {
            statistic = Float.valueOf(statistic.toString()) / 1000;
        }
        FileUtils.appendToFile(fileName, "YVALUE" + "=" + StringUtils.appendIfMissing(String.valueOf(statistic), ""));
    }

    public boolean isHeadlessMode() {
        return headlessMode;
    }

    public void setHeadlessMode(boolean headlessMode) {
        this.headlessMode = headlessMode;
    }

    private String decrypt(final String source) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            final SecretKeySpec secretKey = new SecretKeySpec(Constants.SECURITY_KEY, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(source)));
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    protected String encrypt(final String source) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            final SecretKeySpec secretKey = new SecretKeySpec(Constants.SECURITY_KEY, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return org.apache.commons.codec.binary.Base64.encodeBase64String(cipher.doFinal(source.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
