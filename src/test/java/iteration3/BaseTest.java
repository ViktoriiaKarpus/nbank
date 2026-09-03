package iteration3;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import requests.steps.AdminSteps;
import requests.steps.TestDataStorage;

public class BaseTest {

    protected SoftAssertions softly;

    @BeforeEach
    public void setupTest() {
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void afterTest() {
        softly.assertAll();

        for (Long userId : TestDataStorage.getCreatedUserIds()) {
            try {
                AdminSteps.deleteUser(userId);
            } catch (Exception e) {
                System.err.println("Cleanup failed for user " + userId + ": " + e.getMessage());
            }
        }
        TestDataStorage.clear();
    }
}
