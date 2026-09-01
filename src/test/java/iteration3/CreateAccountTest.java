package iteration3;

import generators.RandomData;
import models.CreateAccountRequest;
import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.UserRole;
import org.hamcrest.Matchers;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class CreateAccountTest extends BaseTest{

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated()
        ).post(userRequest);

        CreateAccountResponse response = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated()
        )
                .post(new CreateAccountRequest());

        assertThat(response.getId(), Matchers.notNullValue());
        assertThat(response.getBalance(), equalTo(0.0));

    }
}
