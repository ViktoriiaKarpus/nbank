package iteration3;

import generators.RandomData;
import io.restassured.RestAssured;
import models.*;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequester;
import requests.CustomerProfileRequester;
import requests.LoginUserRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static org.hamcrest.MatcherAssert.assertThat;
import static specs.RequestSpecs.AUTHORIZATION_HEADER;

public class ChangeTheUserNameTest extends BaseTest {

    private CreateUserRequest createRandomUser() {
        return CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();
    }

    private String createAndLoginUser(CreateUserRequest createRequest) {
        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated()
        ).post(createRequest);

        LoginUserRequest loginRequest = LoginUserRequest.builder()
                .username(createRequest.getUsername())
                .password(createRequest.getPassword())
                .build();

        return new LoginUserRequester(
                RequestSpecs.unauthSpec(),
                ResponseSpecs.requestReturnsOK()
        )
                .post(loginRequest)
                .extract()
                .header(AUTHORIZATION_HEADER);
    }

    @Test
    public void adminCanCreateUserTest() {
        CreateUserRequest request = createRandomUser();

        CreateUserResponse response = new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated()
        )
                .post(request)
                .extract()
                .as(CreateUserResponse.class);

        assertThat(response.getUsername(), Matchers.equalTo(request.getUsername()));
        assertThat(response.getRole(), Matchers.equalTo("USER"));

    }

    @Test
    public void userCanLoginWithValidDataTest() {
        CreateUserRequest createRequest = createRandomUser();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated()
        ).post(createRequest);

        LoginUserRequest loginRequest = LoginUserRequest.builder()
                .username(createRequest.getUsername())
                .password(createRequest.getPassword())
                .build();

        new LoginUserRequester(
                RequestSpecs.unauthSpec(),
                ResponseSpecs.requestReturnsOK()
        )
                .post(loginRequest)
                .header(AUTHORIZATION_HEADER, Matchers.notNullValue());
    }

    @Test
    public void getCustomerProfileTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);

        CustomerProfileResponse response = new CustomerProfileRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsOK()
        )
                .get()
                .extract()
                .as(CustomerProfileResponse.class);

        assertThat(response.getUsername(), Matchers.equalTo(createRequest.getUsername()));
        assertThat(response.getRole(), Matchers.equalTo("USER"));
    }

    @Test
    public void updateCustomerProfileTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);

        UpdateCustomerProfileRequest request = UpdateCustomerProfileRequest.builder()
                .name("John Smith")
                .build();

        UpdateCustomerProfileResponse response = new CustomerProfileRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsOK()
        )
                .put(request)
                .extract()
                .as(UpdateCustomerProfileResponse.class);

        assertThat(response.getCustomer().getName(),Matchers.equalTo("John Smith"));
        assertThat(response.getCustomer().getRole(),Matchers.equalTo("USER"));

    }

    @Test
    public void verifyCustomerProfileAfterUpdating() {

        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);

        UpdateCustomerProfileRequest updateRequest = UpdateCustomerProfileRequest.builder()
                .name("John Smith")
                .build();

        CustomerProfileRequester requester = new CustomerProfileRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsOK()
        );

        requester.put(updateRequest);

        CustomerProfileResponse response = requester.get()
                .extract()
                .as(CustomerProfileResponse.class);

        assertThat(response.getName(), Matchers.equalTo("John Smith"));
        assertThat(response.getRole(), Matchers.equalTo("USER"));
    }

    @Test
    public void updateCustomerProfileByAddingJustOneNameTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);

        UpdateCustomerProfileRequest request = UpdateCustomerProfileRequest.builder()
                .name("John")
                .build();

        RestAssured
                .given(RequestSpecs.authWithToken(userAuth))
                .body(request)
                .put("/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Name must contain two words with letters only"));
    }
}
