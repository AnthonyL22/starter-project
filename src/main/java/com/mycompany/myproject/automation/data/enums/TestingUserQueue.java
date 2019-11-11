package com.mycompany.myproject.automation.data.enums;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mycompany.myproject.automation.frameworksupport.type.Account;
import com.mycompany.myproject.automation.frameworksupport.type.AvailableUsers;
import com.pwc.core.framework.FrameworkConstants;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.pwc.logging.service.LoggerService.LOG;

@Component
public class TestingUserQueue {

    private static Queue<Account> userQueue = new ConcurrentLinkedQueue<>(
            getUserPoolFromYaml()
    );

    /**
     * Read pool of users from YAML source file.
     *
     * @return list of available users defined in data file
     */
    private static List getUserPoolFromYaml() {

        AvailableUsers availableUsers = null;
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            URL usersYaml = TestingUserQueue.class.getClassLoader().getResource((String.format("data/%s/users.yaml",
                    System.getProperty(FrameworkConstants.AUTOMATION_TEST_ENVIRONMENT))));
            LOG(false, "Reading users from '%s'", usersYaml.getPath());
            availableUsers = mapper.readValue(usersYaml, AvailableUsers.class);
        } catch (Exception e) {
            LOG(true, "Failed to read user pool due to - %s", e.getMessage());
        }
        return availableUsers.getAccounts();
    }

    public static Account getNextAvailableUser() {
        return userQueue.poll();
    }

    public static void returnUserToQueue(Account userToReturn) {
        userQueue.add(userToReturn);
    }

}
