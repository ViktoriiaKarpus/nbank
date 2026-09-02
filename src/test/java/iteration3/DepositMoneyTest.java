package iteration3;

import generators.RandomData;
import generators.RandomModelGenerator;
import io.restassured.response.ValidatableResponse;
import models.*;
import models.comparison.ModelAssertions;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static specs.RequestSpecs.AUTHORIZATION_HEADER;

public class DepositMoneyTest extends BaseTest {

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

    private double generateValidDepositAmount() {
        return RandomData.getDepositAmount();
    }

    public static Stream<Arguments> depositInvalidData() {
        return Stream.of(
                Arguments.of(5000.1, ResponseSpecs.DEPOSIT_AMOUNT_CANNOT_EXCEED_5000),
                Arguments.of(-1d, ResponseSpecs.DEPOSIT_AMOUNT_MUST_BE_AT_LEAST_001),
                Arguments.of(0.0, ResponseSpecs.DEPOSIT_AMOUNT_MUST_BE_AT_LEAST_001)
        );
    }

    @Test
    public void createAccountTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);

        CreateAccountResponse response = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated()
        )
                .post(new CreateAccountRequest());

        assertThat(response.getId(), Matchers.notNullValue());
        assertThat(response.getBalance(), equalTo(0.0));

    }

    public static Stream<Arguments> depositMinAndMaxAllowedAmountData() {
        return Stream.of(
                Arguments.of(5000.0),
                Arguments.of(0.01)
        );
    }

    @ParameterizedTest
    @MethodSource("depositMinAndMaxAllowedAmountData")
    void userCanDepositAllowedAmountTest(double amount) {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);
        int accountId = createAccount(userAuth);

        DepositRequest request = DepositRequest.builder()
                .id(accountId)
                .balance(amount)
                .build();

        DepositResponse response = new ValidatedCrudRequester<DepositResponse>(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK()
        )
                .post(request);

        assertThat(response.getId(), equalTo(accountId));
        assertThat(response.getBalance(), equalTo(amount));
    }

    @Test
    public void depositValidAmountTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);
        int accountId = createAccount(userAuth);

        double depositAmount = generateValidDepositAmount();

        DepositRequest request = DepositRequest.builder()
                .id(accountId)
                .balance(depositAmount)
                .build();

        DepositResponse response = new ValidatedCrudRequester<DepositResponse>(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK()
        )
                .post(request);

        ModelAssertions.assertThatModels(request, response).match();
        assertThat(response.getBalance(), equalTo(depositAmount));
    }

    @Test
    public void getAccountTransactionsReturnsDepositRecordTest() {

        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);
        int accountId = createAccount(userAuth);

        double depositAmount = generateValidDepositAmount();

        DepositRequest request = DepositRequest.builder()
                .id(accountId)
                .balance(depositAmount)
                .build();

        new ValidatedCrudRequester<DepositResponse>(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK()
        ).post(request);

        TransferResponse[] transactions = new ValidatedCrudRequester<TransferResponse>(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.TRANSACTIONS,
                ResponseSpecs.requestReturnsOK()
        ).getTransactions(accountId);

        assertThat(
                Arrays.stream(transactions).anyMatch(transaction ->
                        transaction.getAmount().equals(depositAmount)
                                && transaction.getType().equals("DEPOSIT")
                                && transaction.getRelatedAccountId().equals((long) accountId)
                ),
                equalTo(true)
        );
    }

    @ParameterizedTest
    @MethodSource("depositInvalidData")
    void userCannotDepositInvalidAmountTest(double amount, String expectedErrorMessage) {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);
        int accountId = createAccount(userAuth);

        DepositRequest request = DepositRequest.builder()
                .id(accountId)
                .balance(amount)
                .build();

        ValidatableResponse response = new CrudRequester(
                RequestSpecs.authWithToken(userAuth),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsBadRequestWithText(expectedErrorMessage)
        ).post(request);

        assertThat(response.extract().statusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
    }
}
