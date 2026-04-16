package iteration3;

import generators.RandomData;
import io.restassured.RestAssured;
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
        return new CreateAccountRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.entityWasCreated()
        )
                .post(new CreateAccountRequest())
                .extract()
                .jsonPath()
                .getInt("id");
    }

    public static Stream<Arguments> depositInvalidData() {
        return Stream.of(
                Arguments.of(5000.1),
                Arguments.of(-1d)
        );
    }

    @Test
    public void createAccountTest() {//done
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
        assertThat(response.getBalance(), Matchers.equalTo(0.0));

    }

    @Test
    public void depositFiveThousandPositiveTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);
        int accountId = createAccount(userAuth);

        DepositRequest request = DepositRequest.builder()
                .id(accountId)
                .balance(5000d)
                .build();

        DepositResponse response = new DepositRequester(
                RequestSpecs.authWithToken(userAuth),
                ResponseSpecs.requestReturnsOK()
        )
                .post(request)
                .extract()
                .as(DepositResponse.class);

        assertThat(response.getId(), Matchers.equalTo(accountId));
        assertThat(response.getBalance(), Matchers.equalTo(5000.0));

    }

    @Test
    public void verifyAccountTransactionsAfterDepositingFiveThousandTest() {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);
        int accountId = createAccount(userAuth);

        DepositRequest request = DepositRequest.builder()
                .id(accountId)
                .balance(5000d)
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
                        transaction.getAmount().equals(5000.0)
                                && transaction.getType().equals("DEPOSIT")
                                && transaction.getRelatedAccountId().equals((long) accountId)
                ),
                Matchers.equalTo(true)
        );
    }

    @ParameterizedTest
    @MethodSource("depositInvalidData")
    void userCannotDepositInvalidAmountTest(double amount) {
        CreateUserRequest createRequest = createRandomUser();
        String userAuth = createAndLoginUser(createRequest);
        int accountId = createAccount(userAuth);

        DepositRequest request = DepositRequest.builder()
                .id(accountId)
                .balance(amount)
                .build();

        RestAssured
                .given(RequestSpecs.authWithToken(userAuth))
                .body(request)
                .post("/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }
}
