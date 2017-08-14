package com.mycompany.myproject.automation.frameworksupport;

import com.mycompany.myproject.automation.data.Constants;
import com.mycompany.myproject.automation.data.Data;
import com.pwc.core.framework.WebTestCase;
import com.pwc.core.framework.command.WebServiceCommand;
import com.pwc.core.framework.data.Credentials;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.List;

public abstract class MyApplicationTestCase extends WebTestCase {

    private Credentials credentials;
    private boolean headlessMode = false;

    @BeforeClass(alwaysRun = true)
    public void login() {

        if (null == getCredentials()) {
            setCredentials(Data.DEFAULT_USER_CREDENTIALS);
        }

        if (!isHeadlessMode() &&
                (getCredentials().getUsername() != null && getCredentials().getPassword() != null)) {
            webAction(getCredentials());
        }

        webAction(Constants.LOGO_IMAGE);

    }

    @AfterClass(alwaysRun = true)
    public void logout() {
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
