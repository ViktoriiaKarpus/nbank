package requests.steps;

import generators.RandomModelGenerator;
import models.*;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class AdminSteps {
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

    public static DeleteUserResponse deleteUser(long userId) {

        return new ValidatedCrudRequester<DeleteUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.DELETE_USER,
                ResponseSpecs.userDeletedSuccessfully(userId)
        )
                .delete(userId);
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

    public static int createAccount(String userAuth) {
        CreateAccountResponse response = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated()
        )
                .post(new CreateAccountRequest());

        return (int) response.getId();
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
}
