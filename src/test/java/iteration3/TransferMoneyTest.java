package iteration3;

import generators.RandomData;
import models.*;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import requests.*;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static specs.RequestSpecs.AUTHORIZATION_HEADER;

import io.restassured.response.ValidatableResponse;

public class TransferMoneyTest extends BaseTest {

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

    private int createAccount(String userAuth) {
        CreateAccountResponse response = new CreateAccountRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.entityWasCreated()
        )
                .post(new CreateAccountRequest())
                .extract()
                .as(CreateAccountResponse.class);

        return (int) response.getId();
    }

    private void depositMoney(String userAuth, int accountId, double amount) {
        DepositRequest request = DepositRequest.builder()
                .id(accountId)
                .balance(amount)
                .build();

        new DepositRequester(
                RequestSpecs.authWithToken(userAuth),
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
        new TransferMoneyRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsOK()
        )
                .post(request)
                .body(Matchers.containsString("Transfer successful"));
    }

    @Test
    public void userCannotTransferMoreThan10000Test() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);
        int accountId = createAccount(userAuth);

        double bigAmount = 10000.00;

        DepositRequest request = DepositRequest.builder()
                .id(accountId)
                .balance(bigAmount)
                .build();

        ValidatableResponse response = new DepositRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsBadRequestWithText(ResponseSpecs.DEPOSIT_AMOUNT_CANNOT_EXCEED_5000)
        ).post(request);

        assertThat(response.extract().statusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
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

        ValidatableResponse response = new TransferMoneyRequester(
                RequestSpecs.authWithToken(userAuth),
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

        ValidatableResponse response = new TransferMoneyRequester(
                RequestSpecs.unauthSpec(),
                ResponseSpecs.requestReturnsUnauthorized()
        )
                .post(request);

        assertThat(response.extract().statusCode(), equalTo(HttpStatus.SC_UNAUTHORIZED));
    }
}
