package iteration3;

import generators.RandomData;
import models.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
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

    private String createAndLoginUser(CreateUserRequest createRequest) {//переделан
        new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated()
        ).post(createRequest);

        LoginUserRequest loginRequest = LoginUserRequest.builder()
                .username(createRequest.getUsername())
                .password(createRequest.getPassword())
                .build();

        return new ValidatedCrudRequester<LoginUserResponse>(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK()
        ).postAndGetHeader(loginRequest, AUTHORIZATION_HEADER);
    }

    @Test
    public void adminCanCreateUserTest() {
        CreateUserRequest request = createRandomUser();

        CreateUserResponse response = new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated()
        )
                .post(request);

        assertThat(response.getUsername(), Matchers.equalTo(request.getUsername()));
        assertThat(response.getRole(), Matchers.equalTo(UserRole.USER.name()));
        assertThat(response.getId(), Matchers.notNullValue());
    }

    @Test
    public void userCanLoginWithValidDataTest() {
        CreateUserRequest createRequest = createRandomUser();

        new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated()
        ).post(createRequest);

        LoginUserRequest loginRequest = LoginUserRequest.builder()
                .username(createRequest.getUsername())
                .password(createRequest.getPassword())
                .build();

        new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK()
        )
                .post(loginRequest)
                .header(AUTHORIZATION_HEADER, Matchers.notNullValue());
    }

    @Test
    public void getCustomerProfileTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);

        CustomerProfileResponse response = new ValidatedCrudRequester<CustomerProfileResponse>(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsOK()
        )
                .get();

        assertThat(response.getUsername(), Matchers.equalTo(createRequest.getUsername()));
        assertThat(response.getRole(), Matchers.is(UserRole.USER));
    }

    @Test
    public void updateCustomerProfileTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);

        String randomName = RandomData.getFullName();

        UpdateCustomerProfileRequest request =
                UpdateCustomerProfileRequest.builder()
                        .name(randomName)
                        .build();

        UpdateCustomerProfileResponse response =
                new ValidatedCrudRequester<UpdateCustomerProfileResponse>(
                        RequestSpecs.authWithToken(userAuth),
                        Endpoint.UPDATE_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK()
                )
                        .update(request);

        assertThat(
                response.getCustomer().getName(),
                Matchers.equalTo(randomName)
        );

        assertThat(
                response.getCustomer().getRole(),
                Matchers.equalTo(UserRole.USER.name())
        );
    }

    @Test
    public void verifyCustomerProfileAfterUpdating() {

        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);

        String randomName = RandomData.getFullName();

        UpdateCustomerProfileRequest updateRequest = UpdateCustomerProfileRequest.builder()
                .name(randomName)
                .build();


        new ValidatedCrudRequester<UpdateCustomerProfileResponse>(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.UPDATE_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsOK()
        )
                .update(updateRequest);

        CustomerProfileResponse response =
                new ValidatedCrudRequester<CustomerProfileResponse>(
                        RequestSpecs.authWithToken(userAuth),
                        Endpoint.CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK()
                )
                        .get();

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

        new CrudRequester(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsBadRequestWithText(ResponseSpecs.NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY)
        )
                .update(request);
    }


    @Test
    public void updateCustomerProfileByAddingJustOneNameNameNotChangedTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);

        CustomerProfileResponse initialProfile =
                new ValidatedCrudRequester<CustomerProfileResponse>(
                        RequestSpecs.authWithToken(userAuth),
                        Endpoint.CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK()
                )
                        .get();

        String initialName = initialProfile.getName();

        String invalidName = RandomData.getUsername();

        UpdateCustomerProfileRequest invalidRequest =
                UpdateCustomerProfileRequest.builder()
                        .name(invalidName)
                        .build();

        new CrudRequester(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.UPDATE_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsBadRequestWithText(
                        ResponseSpecs.NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY
                )
        )
                .update(invalidRequest);

        CustomerProfileResponse currentProfile =
                new ValidatedCrudRequester<CustomerProfileResponse>(
                        RequestSpecs.authWithToken(userAuth),
                        Endpoint.CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK()
                )
                        .get();

        assertThat(
                currentProfile.getName(),
                Matchers.equalTo(initialName)
        );
    }
}
