package iteration3;

import generators.RandomModelGenerator;
import models.CreateUserRequest;
import models.CreateUserResponse;
import org.junit.jupiter.api.Test;
import requests.steps.AdminSteps;

public class DeleteUserTest extends BaseTest{

    @Test
    public void adminCanDeleteUserByIdTest() {
        CreateUserRequest createRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        CreateUserResponse createdUser =
                AdminSteps.createUserFromRequest(createRequest);

        long userId = createdUser.getId();

        AdminSteps.deleteUser(userId);
    }
}
