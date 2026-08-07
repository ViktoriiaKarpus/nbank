package iteration3;

import generators.RandomData;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.UserRole;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequester;
import requests.AdminDeleteUserRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static org.hamcrest.MatcherAssert.assertThat;

public class DeleteUserTest extends BaseTest{

    @Test
    public void adminCanDeleteUserByIdTest() {
        CreateUserRequest createRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        CreateUserResponse createdUser = new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated()
        )
                .post(createRequest)
                .extract()
                .as(CreateUserResponse.class);

        long userId = createdUser.getId();

        new AdminDeleteUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.requestReturnsOK()
        )
                .delete(userId);
    }

    @Test
    public void deletedUserAreNotFound() {
        CreateUserRequest createRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        CreateUserResponse createdUser = new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated()
        )
                .post(createRequest)
                .extract()
                .as(CreateUserResponse.class);

        long userId = createdUser.getId();

        new AdminDeleteUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.requestReturnsOK()
        )
                .delete(userId);

        CreateUserResponse[] allUsers = new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.requestReturnsOK()
        )
                .getAllUsers()
                .extract()
                .as(CreateUserResponse[].class);

        boolean userStillExists = java.util.Arrays.stream(allUsers)
                .anyMatch(user -> user.getId() == userId);

        assertThat(userStillExists, Matchers.is(false));
    }
}
