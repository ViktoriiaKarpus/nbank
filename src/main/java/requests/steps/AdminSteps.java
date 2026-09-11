package requests.steps;

import generators.RandomModelGenerator;
import models.*;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static specs.RequestSpecs.AUTHORIZATION_HEADER;

public class AdminSteps {

    public static String createAndLoginUser(CreateUserRequest createRequest) {//нужно ли тут менять
        AdminSteps.createUserFromRequest(createRequest);//вот тут не понятно

        LoginUserRequest loginRequest = LoginUserRequest.builder()
                .username(createRequest.getUsername())
                .password(createRequest.getPassword())
                .build();

        return new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK()
        ).postAndGetHeader(loginRequest, RequestSpecs.AUTHORIZATION_HEADER);
    }

    public static CreateUserRequest createUser() {
        CreateUserRequest userRequest =
                RandomModelGenerator.generate(CreateUserRequest.class);

        CreateUserResponse response = new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated()).
                post(userRequest);

        TestDataStorage.registerUser(response.getId());

        return userRequest;
    }

    public static CreateUserResponse createUserFromRequest(CreateUserRequest userRequest) {
        CreateUserResponse response = new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated()
        ).post(userRequest);

        TestDataStorage.registerUser(response.getId());

        return response;
    }

    public static int createAccount(String userAuth) {
        CreateAccountResponse response = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated()
        )
                .post(new CreateAccountRequest());

        return (int) response.getId();
    }

    public static DepositResponse makeDeposit(
            String userAuth,
            DepositRequest request) {

        return new ValidatedCrudRequester<DepositResponse>(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK()
        )
                .post(request);
    }

    public static void depositMoney(String userAuth, int accountId, double amount) {
        DepositRequest request = DepositRequest.builder()
                .id(accountId)
                .balance(amount)
                .build();

        new CrudRequester(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK()
        ).post(request);
    }


    public static TransferResponse transferMoney(
            String userAuth,
            TransferRequest request) {

        return new ValidatedCrudRequester<TransferResponse>(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.TRANSFER_MONEY,
                ResponseSpecs.requestReturnsOK()
        )
                .post(request);
    }

    public static UpdateCustomerProfileResponse updateCustomerProfile(
            String userAuth,
            UpdateCustomerProfileRequest updateRequest) {

        return new ValidatedCrudRequester<UpdateCustomerProfileResponse>(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.UPDATE_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsOK()
        )
                .update(updateRequest);
    }

    public static DeleteUserResponse deleteUser(long userId) {

        return new ValidatedCrudRequester<DeleteUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.DELETE_USER,
                ResponseSpecs.userDeletedSuccessfully(userId)
        )
                .delete(userId);
    }
}
