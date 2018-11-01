package com.mycompany.myproject.automation.frameworksupport;

import com.jayway.restassured.path.json.JsonPath;
import com.mycompany.myproject.automation.data.Constants;
import com.pwc.core.framework.FrameworkConstants;
import com.pwc.core.framework.JavascriptConstants;
import com.pwc.core.framework.WebTestCase;
import com.pwc.core.framework.command.WebServiceCommand;
import com.pwc.core.framework.data.Credentials;
import com.pwc.core.framework.util.PropertiesUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Jsoup;
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
import java.util.stream.IntStream;

import static com.pwc.assertion.AssertService.assertFail;
import static com.pwc.logging.service.LoggerService.LOG;

public abstract class MyApplicationTestCase extends WebTestCase {

    private Credentials credentials;
    private boolean headlessMode = false;
    private boolean previouslyCleared = false;

    @BeforeClass(alwaysRun = true)
    public void login() {

        if (credentials == null) {
            fetchEncryptedCredentials();
        }

        if (!isHeadlessMode()) {
            webAction(Constants.LOGO_IMAGE);
        }

    }

    @BeforeMethod(firstTimeOnly = true)
    public void preserveProduction(Method m) {

        if (StringUtils.containsIgnoreCase(System.getProperty(FrameworkConstants.AUTOMATION_TEST_ENVIRONMENT), "prod")) {
            Test currentTest = m.getAnnotation(Test.class);
            List<String> acceptableProductionGroups = Arrays.asList(Groups.ACCEPTANCE_TEST, Groups.NEURAL_NETWORK_TEST);
            if (!CollectionUtils.containsAny(Arrays.asList(currentTest.groups()), acceptableProductionGroups)) {
                assertFail("PREVENTING EXECUTION OF REGRESSION TEST='%s' IN PRODUCTION", m.getName());
                tearDownClass();
                System.exit(1);
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void logout() {
    }

    /**
     * Generate and fetch Credentials.  Will attempt to login with Encrypted credentials first and if that fails
     * try the none-version controlled credentials in a file in resources dir named "password.properties"
     */
    private void fetchEncryptedCredentials() {
        try {
            credentials = new Credentials(System.getenv(Constants.ENCRYPTED_SERVICE_USER_NAME_PROPERTY), System.getenv(Constants.ENCRYPTED_SERVICE_USER_PASS_PROPERTY));
        } catch (Exception e) {
            LOG(true, "Unable to locate Credentials for user='%s' in environment vars.", System.getenv(Constants.ENCRYPTED_SERVICE_USER_NAME_PROPERTY), e);
        }

        if (StringUtils.isEmpty(credentials.getPassword())) {
            Properties tempProperties = PropertiesUtils.getPropertiesFromPropertyFile(Constants.LOCAL_CREDENTIALS_FILENAME);
            credentials.setUsername(tempProperties.getProperty("service_username"));
            credentials.setPassword(tempProperties.getProperty("service_password"));
        }
    }

    /**
     * Send a REST ws action to a service End Point
     *
     * @param command BaseGetCommand command type
     */
    protected Object webServiceAction(final WebServiceCommand command) {
        return super.webServiceAction(command, null);
    }

    /**
     * Send a REST ws action to a service End Point
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
     * Send a REST ws action to a service End Point
     *
     * @param command      BaseGetCommand command type
     * @param parameterMap Name-Value pair filled map of parameters to send in HTTP request
     */
    protected Object webServiceAction(final WebServiceCommand command, final HashMap<String, Object> parameterMap) {
        return super.webServiceAction(command, null, parameterMap);
    }

    /**
     * Get all URLs from a given sitemap file
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
     * Extract all text from the HTML page
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
     * Convert XML to JSON from driver version APIs
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
     * Get a random snippet of a given String from the 0 index to a random location in the given <code>String</code>
     *
     * @param displayedColumnValue base value to get a snippet of
     * @return sub-string of original value
     */
    protected String getRandomBeginningSubString(String displayedColumnValue) {
        int randomEnd = 1 + (int) (Math.random() * displayedColumnValue.length() - 1);
        return StringUtils.substring(displayedColumnValue, 0, randomEnd);
    }

    /**
     * Get a random fragment of a given sentence of words
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
     * Get a random snippet of a given String from a random location in the given <code>String</code> to the end
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
     * Get a random sub-list of a given values from a random set in the given <code>List</code> to the end
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
     * Clear opt-in popups unexpectedly displayed to user
     */
    protected void clearPopup() {

        int RETRY_COUNT = 20;
        if (!previouslyCleared) {
            if (null != webEventController) {
                IntStream.range(0, RETRY_COUNT).forEach(i -> {
                    if (StringUtils.isNoneEmpty(getWebElementText("//div[@id='my_popup']"))) {
                        executeJavascript(JavascriptConstants.CLICK_BY_XPATH, "//div[@id='my_popup']");
                        previouslyCleared = true;
                        return;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        previouslyCleared = false;
    }

    public boolean isHeadlessMode() {
        return headlessMode;
    }

    public void setHeadlessMode(boolean headlessMode) {
        this.headlessMode = headlessMode;
    }

    protected Credentials getCredentials() {
        return credentials;
    }

    protected void setCredentials(Credentials encryptedCredentials) {
        this.credentials = new Credentials(decrypt(encryptedCredentials.getUsername()), decrypt(encryptedCredentials.getPassword()));
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
