package com.mycompany.myproject.automation.data;

public final class Constants {

    private Constants() {
    }

    // Application Defaults
    public static final byte[] SECURITY_KEY = {0x74, 0x68, 0x69, 0x73, 0x49, 0x73, 0x41, 0x53, 0x65, 0x63, 0x72, 0x65, 0x74, 0x4b, 0x65, 0x79};
    public static final int MAX_WEB_SERVICE_RESPONSE_TIME = 3000;
    public static final String LOAD_TEST_RESULTS_DIRECTORY = "performance/";

    // URLs
    public static final String SEARCH_URL = "/#search";
    public static final String ADVANCED_SEARCH_URL = "/#advancedsearch";

    // IMAGE Elements
    public static final String LOGO_ANCHOR_IMAGE = "//img[@class='logo']";

    // ANCHOR Elements
    public static final String VARIABLE_BY_TEXT_ANCHOR = "//a[text()='%s']";
    public static final String HELP_ANCHOR = "//a[contains(@href, 'help')]";
    public static final String QUICK_STATS_ANCHOR = "QUICK STATS";

    // INPUT Elements
    public static final String KEYWORD_INPUT = "KEYWORD";
    public static final String GROUP_ID_INPUT = "groupId";
    public static final String SEARCH_INPUT = "//input[@id='search']";

    // BUTTON Elements
    public static final String COORDINATE_SEARCH_BUTTON = "gavSearchButton";

    // DIV Elements
    public static final String STATISTICS_DIV = "statsText";

    // STATIC Text
    public static final String NO_RECORDS_FOUND_TEXT = "No records found, try new search criteria.";

}
