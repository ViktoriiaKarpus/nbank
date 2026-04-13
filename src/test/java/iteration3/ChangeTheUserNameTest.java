package iteration3;

import generators.RandomData;
import io.restassured.RestAssured;
import models.CreateUserRequest;
import models.LoginUserRequest;
import models.UpdateCustomerProfileRequest;
import models.UserRole;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequester;
import requests.CustomerProfileRequester;
import requests.LoginUserRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

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
                .header("Authorization");
    }

    @Test
    public void adminCanCreateUserTest() {
        CreateUserRequest request = createRandomUser();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated()
        )
                .post(request)
                .body("username", Matchers.equalTo(request.getUsername()))
                .body("role", Matchers.equalTo("USER"));
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
                .header("Authorization", Matchers.notNullValue());
    }

    @Test
    public void getCustomerProfileTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);

        new CustomerProfileRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsOK()
        )
                .get()
                .body("username", Matchers.equalTo(createRequest.getUsername()))
                .body("role", Matchers.equalTo("USER"));
    }

    @Test
    public void updateCustomerProfileTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);

        UpdateCustomerProfileRequest request = UpdateCustomerProfileRequest.builder()
                .name("John Smith")
                .build();

        new CustomerProfileRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsOK()
        )
                .put(request)
                .body("customer.name", Matchers.equalTo("John Smith"))
                .body("customer.role", Matchers.equalTo("USER"));
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

        requester.get()
                .body("name", Matchers.equalTo("John Smith"))
                .body("role", Matchers.equalTo("USER"));
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
