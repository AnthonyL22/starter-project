package com.mycompany.myproject.automation.data;

public final class Constants {

    private Constants() {
    }

    // Application Defaults
    public static final byte[] SECURITY_KEY = {0x74, 0x68, 0x69, 0x73, 0x49, 0x73, 0x41, 0x53, 0x65, 0x63, 0x72, 0x65, 0x74, 0x4b, 0x65, 0x79};
    public static final int MAX_WEB_SERVICE_RESPONSE_TIME = 3000;

    // URLs
    public static final String SEARCH_URL = "/#search";
    public static final String ADVANCED_SEARCH_URL = "/#advancedsearch";

    // IMG Elements
    public static final String LOGO_IMAGE = "The Central Repository";

    // ANCHOR Elements
    public static final String VARIABLE_BY_TEXT_ANCHOR = "//a[text()='%s']";
    public static final String CORE_ANCHOR = "core-microservice";
    public static final String RUNNER_ANCHOR = "runner-microservice";
    public static final String QUICK_STATS_ANCHOR = "QUICK STATS";
    public static final String ADVANCED_SEARCH_ANCHOR = "//a[text()='ADVANCED SEARCH']";

    // INPUT Elements
    public static final String QUERY_INPUT = "query";
    public static final String GROUP_ID_INPUT = "groupId";
    public static final String PACKAGING_INPUT = "packaging";

    // BUTTON Elements
    public static final String SEARCH_BUTTON = "Search";
    public static final String COORDINATE_SEARCH_BUTTON = "gavSearchButton";

    // DIV Elements
    public static final String STATISTICS_DIV = "statsText";

    // STATIC Text
    public static final String NO_RECORDS_FOUND_TEXT = "No records found, try new search criteria.";

}
