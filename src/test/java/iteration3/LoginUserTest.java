package iteration3;

import generators.RandomData;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.LoginUserRequest;
import models.UserRole;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static specs.RequestSpecs.AUTHORIZATION_HEADER;

public class LoginUserTest extends BaseTest{
    @Test
    public void adminCanGenerateAuthTokenTest() {
        LoginUserRequest userRequest = LoginUserRequest.builder()
                .username("admin")
                .password("admin")
                .build();

        new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK()
        ).post(userRequest);
    }

    @Test
    public void userCanGenerateAuthTokenTest() {
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated()
        ).post(userRequest);

        new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK()
        )
                .post(LoginUserRequest.builder()
                        .username(userRequest.getUsername())
                        .password(userRequest.getPassword())
                        .build())
                .header(AUTHORIZATION_HEADER, Matchers.notNullValue());
    }
}
