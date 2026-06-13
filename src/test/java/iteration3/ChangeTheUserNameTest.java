package iteration3;

import generators.RandomData;
import models.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequester;
import requests.CustomerProfileRequester;
import requests.LoginUserRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.List;

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
        assertThat(response.getRole(), Matchers.equalTo(UserRole.USER.name()));
        assertThat(response.getId(), Matchers.notNullValue());
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
        assertThat(response.getRole(), Matchers.is(UserRole.USER));
    }

    @Test
    public void updateCustomerProfileTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);

        String randomName = RandomData.getFullName();

        UpdateCustomerProfileRequest request = UpdateCustomerProfileRequest.builder()
                .name(randomName)
                .build();

        UpdateCustomerProfileResponse response = new CustomerProfileRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsOK()
        )
                .put(request)
                .extract()
                .as(UpdateCustomerProfileResponse.class);

        assertThat(response.getCustomer().getName(), Matchers.equalTo(randomName));
        assertThat(response.getCustomer().getRole(), Matchers.equalTo(UserRole.USER.name()));

    }

    @Test
    public void verifyCustomerProfileAfterUpdating() {

        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);

        String randomName = RandomData.getFullName();

        UpdateCustomerProfileRequest updateRequest = UpdateCustomerProfileRequest.builder()
                .name(randomName)
                .build();

        CustomerProfileRequester requester = new CustomerProfileRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsOK()
        );

        requester.put(updateRequest);

        CustomerProfileResponse response = requester.get()
                .extract()
                .as(CustomerProfileResponse.class);

        assertThat(response.getName(), Matchers.equalTo(updateRequest.getName()));
        assertThat(response.getRole(), Matchers.is(UserRole.USER));
    }

    @Test
    public void updateCustomerProfileByAddingJustOneNameTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);

        String randomName = RandomData.getUsername();

        UpdateCustomerProfileRequest request = UpdateCustomerProfileRequest.builder()
                .name(randomName)
                .build();

        new CustomerProfileRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsBadRequestWithText("Name must contain two words with letters only")
        )
                .put(request);
    }

    @Test
    public void updateCustomerProfileByAddingJustOneNameNameNotChangedTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);

        CustomerProfileRequester requester = new CustomerProfileRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsOK()
        );

        CustomerProfileResponse initialProfile = requester.get()
                .extract()
                .as(CustomerProfileResponse.class);

        String initialName = initialProfile.getName();

        String invalidName = RandomData.getUsername();

        UpdateCustomerProfileRequest invalidRequest = UpdateCustomerProfileRequest.builder()
                .name(invalidName)
                .build();

        new CustomerProfileRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsBadRequestWithText("Name must contain two words with letters only")
        )
                .put(invalidRequest);

        CustomerProfileResponse currentProfile = requester.get()
                .extract()
                .as(CustomerProfileResponse.class);

        assertThat(currentProfile.getName(), Matchers.equalTo(initialName));
    }
}
