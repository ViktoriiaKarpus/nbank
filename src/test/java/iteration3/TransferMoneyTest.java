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

    @Test
    public void transferMoneyFromTheFirstAccountToTheSecondAccountTest1() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);
        int senderAccountId = createAccount(userAuth);
        int receiverAccountId = createAccount(userAuth);
        depositMoney(userAuth, senderAccountId, 5000.00);
        TransferRequest request = TransferRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(5000.00)
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

        DepositRequest request = DepositRequest.builder()
                .id(accountId)
                .balance(10000.00)
                .build();

        ValidatableResponse response = new DepositRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsBadRequestWithText("Deposit amount cannot exceed 5000")
        ).post(request);

        assertThat(response.extract().statusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    public void userCannotTransferMoneyToNonExistingAccountTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);

        int senderAccountId = createAccount(userAuth);
        depositMoney(userAuth, senderAccountId, 5000.00);

        TransferRequest request = TransferRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(999999)
                .amount(250.75)
                .build();

        ValidatableResponse response = new TransferMoneyRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsBadRequestWithText("Invalid transfer: insufficient funds or invalid accounts")
        ).post(request);

        assertThat(response.extract().statusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
    }
}
