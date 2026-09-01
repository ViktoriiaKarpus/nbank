package iteration3;

import generators.RandomData;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.DeleteUserResponse;
import models.UserRole;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class DeleteUserTest extends BaseTest{

    @Test
    public void adminCanDeleteUserByIdTest() {
        CreateUserRequest createRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        CreateUserResponse createdUser = new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated()
        )
                .post(createRequest);

        long userId = createdUser.getId();

        new ValidatedCrudRequester<DeleteUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.DELETE_USER,
                ResponseSpecs.requestReturnsOK()
        )
                .delete(userId);
    }
}
