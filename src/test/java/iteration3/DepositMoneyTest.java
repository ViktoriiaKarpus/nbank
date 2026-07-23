package iteration3;

import generators.RandomData;
import io.restassured.response.ValidatableResponse;
import models.*;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.AdminCreateUserRequester;
import requests.CreateAccountRequester;
import requests.DepositRequester;
import requests.LoginUserRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static specs.RequestSpecs.AUTHORIZATION_HEADER;

public class DepositMoneyTest extends BaseTest {

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

    private double generateValidDepositAmount() {
        return RandomData.getDepositAmount();
    }

    public static Stream<Arguments> depositInvalidData() {
        return Stream.of(
                Arguments.of(5000.1, "Deposit amount cannot exceed 5000"),
                Arguments.of(-1d, "Deposit amount must be at least 0.01"),
                Arguments.of(0.0, "Deposit amount must be at least 0.01")
        );
    }

    @Test
    public void createAccountTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);

        CreateAccountResponse response = new CreateAccountRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.entityWasCreated()
        )
                .post(new CreateAccountRequest())
                .extract()
                .as(CreateAccountResponse.class);

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

        DepositResponse response = new DepositRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsOK()
        )
                .post(request)
                .extract()
                .as(DepositResponse.class);

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

        DepositResponse response = new DepositRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsOK()
        )
                .post(request)
                .extract()
                .as(DepositResponse.class);

        assertThat(response.getId(), equalTo(accountId));
        assertThat(response.getBalance(), equalTo(depositAmount));
    }

    @Test
    public void verifyAccountTransactionsAfterDepositingFiveThousandTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);
        int accountId = createAccount(userAuth);

        double depositAmount = generateValidDepositAmount();

        DepositRequest request = DepositRequest.builder()
                .id(accountId)
                .balance(depositAmount)
                .build();

        new DepositRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsOK()
        ).post(request);

        TransactionResponse[] transactions = new DepositRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsOK()
        )
                .getTransactions(accountId)
                .extract()
                .as(TransactionResponse[].class);

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

        ValidatableResponse response = new DepositRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsBadRequestWithText(expectedErrorMessage)
        ).post(request);

        assertThat(response.extract().statusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
    }
}
