package iteration3;

import generators.RandomData;
import generators.RandomModelGenerator;
import models.*;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static specs.RequestSpecs.AUTHORIZATION_HEADER;

import io.restassured.response.ValidatableResponse;

public class TransferMoneyTest extends BaseTest {

    private CreateUserRequest createRandomUser() {
        return RandomModelGenerator.generate(CreateUserRequest.class);
    }

    private String createAndLoginUser(CreateUserRequest createRequest) {
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

    private int createAccount(String userAuth) {
        CreateAccountResponse response = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated()
        )
                .post(new CreateAccountRequest());

        return (int) response.getId();
    }

    private void depositMoney(String userAuth, int accountId, double amount) {
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

    private double generateValidTransferAmount() {
        return RandomData.getTransferAmount();
    }

    @Test
    public void transferMoneyFromTheFirstAccountToTheSecondAccountTest1() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);
        int senderAccountId = createAccount(userAuth);
        int receiverAccountId = createAccount(userAuth);

        double transferAmount = generateValidTransferAmount();

        depositMoney(userAuth, senderAccountId, transferAmount);

        TransferRequest request = TransferRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(transferAmount)
                .build();

        new ValidatedCrudRequester<TransferRequest>(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.TRANSFER_MONEY,
                ResponseSpecs.requestReturnsOK()
        )
                .post(request);
    }

    @Test
    public void userCannotTransferMoreThan10000Test() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);

        int senderAccountId = createAccount(userAuth);
        int receiverAccountId = createAccount(userAuth);

        double bigAmount = 10000.01;

        TransferRequest request = TransferRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(bigAmount)
                .build();

        ValidatableResponse response = new CrudRequester(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.TRANSFER_MONEY,
                ResponseSpecs.requestReturnsBadRequestWithText(ResponseSpecs.TRANSFER_AMOUNT_CANNOT_EXCEED_10000)
        ).post(request);

        assertThat(
                response.extract().statusCode(),
                equalTo(HttpStatus.SC_BAD_REQUEST)
        );
    }

    @Test
    public void userCannotTransferMoneyToNonExistingAccountTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);

        int senderAccountId = createAccount(userAuth);

        double depositAmount = generateValidTransferAmount();

        depositMoney(userAuth, senderAccountId, depositAmount);

        TransferRequest request = TransferRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(999999)
                .amount(generateValidTransferAmount())
                .build();

        ValidatableResponse response = new CrudRequester(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.TRANSFER_MONEY,
                ResponseSpecs.requestReturnsBadRequestWithText(ResponseSpecs.INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS)
        ).post(request);

        assertThat(response.extract().statusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    public void userCannotTransferMoneyWithoutAuthorizationTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);
        int senderAccountId = createAccount(userAuth);
        int receiverAccountId = createAccount(userAuth);

        double transferAmount = generateValidTransferAmount();

        depositMoney(userAuth, senderAccountId, transferAmount);

        TransferRequest request = TransferRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(transferAmount)
                .build();

        ValidatableResponse response = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.TRANSFER_MONEY,
                ResponseSpecs.requestReturnsUnauthorized()
        )
                .post(request);

        assertThat(response.extract().statusCode(), equalTo(HttpStatus.SC_UNAUTHORIZED));
    }
}
