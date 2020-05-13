package com.mycompany.myproject.automation.frameworksupport;

import com.mycompany.myproject.automation.data.Constants;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static com.pwc.assertion.AssertService.assertFail;
import static com.pwc.logging.service.LoggerService.LOG;

public abstract class AIUserTestCase extends MyApplicationTestCase {

    private static final long MAXIMUM_USER_EXPERIENCE = 120000;
    private static final String HIGH_LEVEL = "HIGH";
    private static final String MEDIUM_LEVEL = "MEDIUM";
    private static final String LOW_LEVEL = "LOW";
    private static final String COMMON_SEARCH_TERM = "apache";

    public void genericSearch() {
        LOG(true, "---------- Generic Search. Likelihood=%s ----------", HIGH_LEVEL);
        webElementVisible(Constants.LOGO_ANCHOR_IMAGE);
        webAction(Constants.KEYWORD_INPUT, COMMON_SEARCH_TERM);
        webAction(Constants.SEARCH_INPUT);
        webElementTextEquals(combine(Constants.VARIABLE_BY_TEXT_ANCHOR, COMMON_SEARCH_TERM), COMMON_SEARCH_TERM);
    }

    public void advancedSearch() {
        LOG(true, "---------- Advanced Search. Likelihood=%s ----------", HIGH_LEVEL);
        redirect(Constants.ADVANCED_SEARCH_URL);
        webAction(Constants.GROUP_ID_INPUT, "com.pacificwebconsulting.core");
        webAction(Constants.COORDINATE_SEARCH_BUTTON);
        webElementTextEquals(Constants.KEYWORD_INPUT, "g:\"com.pacificwebconsulting.core\"");
    }

    public void quickStats() {
        LOG(true, "---------- View Quick Stats. Likelihood=%s ----------", LOW_LEVEL);
        webAction(Constants.QUICK_STATS_ANCHOR);
        webElementVisible(Constants.STATISTICS_DIV);
    }

    public void securityIntrusion() {
        LOG(true, "---------- SQL Injection from Hacker. Likelihood=%s ----------", MEDIUM_LEVEL);
        redirect(Constants.SEARCH_URL);
        webAction(Constants.KEYWORD_INPUT, "apache*select");
        webAction(Constants.SEARCH_INPUT);
        webElementVisible(Constants.NO_RECORDS_FOUND_TEXT);
    }

    /**
     * Perform a User's activity based on decision.
     *
     * @param decision decision percentage
     * @return pass or fail of activity
     */
    protected void performUserActivityBasedOnADecision(final Map<Integer, String> probabilities, final StopWatch visit, float decision) {
        if (visit.isRunning()) {
            visit.stop();
        }
        if (visit.getTotalTimeMillis() < MAXIMUM_USER_EXPERIENCE) {
            visit.start();
            int myDecision = calculateTheMostLikelyDecision(probabilities, decision);
            runMethodDefinedInMap(probabilities.get(myDecision));
        } else {
            LOG(true, "Test Execution Time of %s ms. has Expired", MAXIMUM_USER_EXPERIENCE);
            tearDownClass();
        }
    }

    /**
     * Find the most likely / closest decision to what you will make.
     *
     * @param decision decision percentage
     * @return most likely <code>Integer</code> decision index
     */
    private int calculateTheMostLikelyDecision(final Map<Integer, String> probabilities, final float decision) {

        List<Integer> probabilitiesAsList = probabilities.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());

        float distance = Math.abs(probabilitiesAsList.get(0) - decision);
        int idx = 0;
        for (int c = 1; c < probabilitiesAsList.size(); c++) {
            float distanceAsFloat = Math.abs(probabilitiesAsList.get(c) - decision);
            if (distanceAsFloat < distance) {
                distance = distanceAsFloat;
                idx = c;
            }
        }
        return probabilitiesAsList.get(idx);
    }

    /**
     * Random percentage within a range of integer values.
     *
     * @param lower lower bound number
     * @param upper upper bound number
     * @return percentage within range
     */
    protected static float randomPercentageInRange(final int lower, final int upper) {
        Random r = new Random();
        return lower + r.nextFloat() * (upper - lower);
    }

    /**
     * Random number within a range of integer values.
     *
     * @param lower lower bound number
     * @param upper upper bound number
     * @return number within range (including either lower or upper bound)
     */
    private static int randomNumericInRange(final int lower, final int upper) {
        Random random = new Random();
        return (int) (random.nextDouble() * ((upper + 1) - lower)) + lower;
    }

    /**
     * Use recursion to call a method.
     *
     * @param methodToCall method to call based on probability
     */
    private void runMethodDefinedInMap(final String methodToCall) {
        try {
            Method x = this.getClass().getMethod(methodToCall);
            x.invoke(this);
        } catch (Exception e) {
            assertFail("Failed to perform method=%s", methodToCall, e);
        }
    }

}
