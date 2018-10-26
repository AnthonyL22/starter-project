package com.mycompany.myproject.automation.tests.load;

import com.mycompany.myproject.automation.data.Constants;
import com.pwc.core.framework.util.FileUtils;
import com.pwc.core.framework.util.PropertiesUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.pwc.logging.service.LoggerService.LOG;

public class LoadTestingHarness extends Thread {

    private static final int NUMBER_OF_USERS = 1;
    private static final int TEST_INVOCATION_COUNT = 1;
    private static final long TIME_BETWEEN_TESTS_IN_MILLISECONDS = 1000;
    private static final long RAMP_UP_TIME_BETWEEN_USERS_IN_MILLISECONDS = 1000;
    protected static final String TEST_RESULT_FILE = "load_test_results.txt";

    private Thread t;
    private String threadName;

    private LoadTestingHarness(String name) {
        threadName = name;
        LOG(true, "Creating Thread named=%s", threadName);
    }

    @Override
    public void run() {

        LOG(true, "Running thread named=%s", threadName);

        try {
            for (int i = TEST_INVOCATION_COUNT; i > 0; i--) {

                LOG(true, "Thread: %s, %s", threadName, i);

                MyLoadTest test = new MyLoadTest();
                test.setUpRunner();
                test.beforeTest();
                test.login();
                test.performLoad();
                test.tearDownClass();

                // Let the thread sleep for a while.
                Thread.sleep(TIME_BETWEEN_TESTS_IN_MILLISECONDS);

            }
        } catch (Exception e) {
            LOG(true, "Thread %s interrupted due to exception=%s", threadName, e);
        }
        LOG(true, "Thread %s exiting.", threadName);

    }

    @Override
    public void start() {
        LOG(true, "Starting Thread named=%s", threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }

    public static void main(String[] args) {

        setupTestResults(TEST_RESULT_FILE);
        List<LoadTestingHarness> tests = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_USERS; i++) {
            LoadTestingHarness test = new LoadTestingHarness("Thread-" + i);
            tests.add(test);
        }

        try {
            for (LoadTestingHarness test : tests) {
                Thread.sleep(RAMP_UP_TIME_BETWEEN_USERS_IN_MILLISECONDS);
                test.start();
            }
        } catch (Exception e) {
            LOG(true, "Thread interrupted due to exception=%s", e);
        }

    }

    /**
     * Cleanup performance results file
     *
     * @param fileName results file to delete
     */
    private static void setupTestResults(final String fileName) {

        String fileToDelete = Constants.LOAD_TEST_RESULTS_DIRECTORY + fileName;
        File stats = PropertiesUtils.getFileFromResources(fileToDelete);
        if (stats != null) {
            FileUtils.deleteFile(fileToDelete);
        }
    }

}
