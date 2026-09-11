package iteration3;

import generators.RandomData;
import generators.RandomModelGenerator;
import models.*;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import io.restassured.response.ValidatableResponse;

import java.util.Arrays;

public class TransferMoneyTest extends BaseTest {

    @Test
    public void transferMoneyFromTheFirstAccountToTheSecondAccountTest1() {
        CreateUserRequest createRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        String userAuth = AdminSteps.createAndLoginUser(createRequest);

        int senderAccountId = AdminSteps.createAccount(userAuth);
        int receiverAccountId = AdminSteps.createAccount(userAuth);

        double transferAmount = RandomData.getTransferAmount();

        DepositRequest depositRequest = DepositRequest.builder()
                .id(senderAccountId)
                .balance(transferAmount)
                .build();

        AdminSteps.makeDeposit(userAuth, depositRequest);

        TransferRequest request = TransferRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(transferAmount)
                .build();

        AdminSteps.transferMoney(userAuth, request);
    }

    @Test
    public void userCannotTransferMoreThan10000Test() {
        CreateUserRequest createRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        String userAuth = AdminSteps.createAndLoginUser(createRequest);

        int senderAccountId = AdminSteps.createAccount(userAuth);
        int receiverAccountId = AdminSteps.createAccount(userAuth);

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

        TransferResponse[] senderTransactions = new ValidatedCrudRequester<TransferResponse>(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.TRANSACTIONS,
                ResponseSpecs.requestReturnsOK()
        ).getTransactions(senderAccountId);

        assertThat(
                Arrays.stream(senderTransactions).noneMatch(t -> t.getType().equals("TRANSFER")),
                equalTo(true)
        );
    }

    @Test
    public void userCannotTransferMoneyToNonExistingAccountTest() {
        CreateUserRequest createRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        String userAuth = AdminSteps.createAndLoginUser(createRequest);

        int senderAccountId = AdminSteps.createAccount(userAuth);

        double depositAmount = RandomData.getTransferAmount();

        AdminSteps.depositMoney(userAuth, senderAccountId, depositAmount);

        TransferRequest request = TransferRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(999999)
                .amount(RandomData.getTransferAmount())
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
        CreateUserRequest createRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        String userAuth = AdminSteps.createAndLoginUser(createRequest);
        int senderAccountId = AdminSteps.createAccount(userAuth);
        int receiverAccountId = AdminSteps.createAccount(userAuth);

        double transferAmount = RandomData.getTransferAmount();

        AdminSteps.depositMoney(userAuth, senderAccountId, transferAmount);

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

        TransferResponse[] receiverTransactions = new ValidatedCrudRequester<TransferResponse>(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.TRANSACTIONS,
                ResponseSpecs.requestReturnsOK()
        ).getTransactions(receiverAccountId);

        assertThat(
                Arrays.stream(receiverTransactions).noneMatch(t -> t.getType().equals("TRANSFER")),
                equalTo(true)
        );
    }
}
