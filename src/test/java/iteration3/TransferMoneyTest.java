package iteration3;

import generators.RandomData;
import io.restassured.RestAssured;
import models.*;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import requests.*;
import specs.RequestSpecs;
import specs.ResponseSpecs;

// класс не работает
//private CreateUserRequest createRandomUser() {
//    return CreateUserRequest.builder()
//            .username(RandomData.getUsername())
//            .password(RandomData.getPassword())
//            .role(UserRole.USER.toString())
//            .build();
//}

//private String createAndLoginUser(CreateUserRequest createRequest) {
//    new AdminCreateUserRequester(
//            RequestSpecs.adminSpec(),
//            ResponseSpecs.entityWasCreated()
//    ).post(createRequest);

//    LoginUserRequest loginRequest = LoginUserRequest.builder()
//            .username(createRequest.getUsername())
//            .password(createRequest.getPassword())
//            .build();

//    return new LoginUserRequester(
//            RequestSpecs.unauthSpec(),
//            ResponseSpecs.requestReturnsOK()
//    )
//            .post(loginRequest)
//            .extract()
//            .header("Authorization");
//}

//private int createAccount(String userAuth) {
//    return new CreateAccountRequester(
//            RequestSpecs.authWithToken(userAuth),
//            ResponseSpecs.entityWasCreated()
//    )
//            .post(new CreateAccountRequest())
//            .extract()
//            .jsonPath()
//            .getInt("id");
//}

//private void depositMoney(String userAuth, int accountId, double amount) {
//    DepositRequest request = DepositRequest.builder()
//            .id(accountId)
//            .balance(amount)
//            .build();

//    new DepositRequester(
//            RequestSpecs.authWithToken(userAuth),
//            ResponseSpecs.requestReturnsOK()
//    ).post(request);
//}

//@Test
//public void transferMoneyFromTheFirstAccountToTheSecondAccountTest() {
//    CreateUserRequest createRequest = createRandomUser();
//    String userAuth = createAndLoginUser(createRequest);

//    int senderAccountId = createAccount(userAuth);
//    int receiverAccountId = createAccount(userAuth);

//    depositMoney(userAuth, senderAccountId, 10000.00);

//    TransferRequest request = TransferRequest.builder()
//            .senderAccountId(senderAccountId)
//            .receiverAccountId(receiverAccountId)
//            .amount(9999.99)
//            .build();

//    new TransferMoneyRequester(
//            RequestSpecs.authWithToken(userAuth),
//            ResponseSpecs.requestReturnsOK()
//    )
//            .post(request)
//            .body(Matchers.containsString("Transfer successful"));
//}

//@Test
//public void verifyAccountTransactionAfterTransfer() {
//    CreateUserRequest createRequest = createRandomUser();
//    String userAuth = createAndLoginUser(createRequest);

//    int senderAccountId = createAccount(userAuth);
//    int receiverAccountId = createAccount(userAuth);

//    depositMoney(userAuth, senderAccountId, 10000.00);

//    TransferRequest request = TransferRequest.builder()
//            .senderAccountId(senderAccountId)
//            .receiverAccountId(receiverAccountId)
//            .amount(9999.99)
//            .build();

//    new TransferMoneyRequester(
//            RequestSpecs.authWithToken(userAuth),
//            ResponseSpecs.requestReturnsOK()
//    ).post(request);

//    new TransferMoneyRequester(
//            RequestSpecs.authWithToken(userAuth),
//            ResponseSpecs.requestReturnsOK()
//    )
//            .getTransactions(senderAccountId)
//            .body("relatedAccountId", Matchers.hasItem(receiverAccountId));
//}

//@Test
//public void transferMoneyFromTheFirstAccountToTheSecondAccount_10000_01() {
//    CreateUserRequest createRequest = createRandomUser();
//    String userAuth = createAndLoginUser(createRequest);

//    int senderAccountId = createAccount(userAuth);
//    int receiverAccountId = createAccount(userAuth);

//    depositMoney(userAuth, senderAccountId, 15000.00);

//    TransferRequest request = TransferRequest.builder()
//            .senderAccountId(senderAccountId)
//            .receiverAccountId(receiverAccountId)
//            .amount(10000.01)
//            .build();

//    RestAssured
//            .given(RequestSpecs.authWithToken(userAuth))
//            .body(request)
//            .post("/api/v1/accounts/transfer") // эндпоинт
//            .then()
//            .statusCode(HttpStatus.SC_BAD_REQUEST)
//            .body(Matchers.containsString("Transfer amount cannot exceed 10000"));
//}

//@Test
//public void transferMoneyToNonExistingAccountTest() {
//    CreateUserRequest createRequest = createRandomUser();
//    String userAuth = createAndLoginUser(createRequest);

//    int senderAccountId = createAccount(userAuth);
//    depositMoney(userAuth, senderAccountId, 5000.00);

//    TransferRequest request = TransferRequest.builder()
//            .senderAccountId(senderAccountId)
//            .receiverAccountId(999999)
//            .amount(250.75)
//            .build();

//    RestAssured
//            .given(RequestSpecs.authWithToken(userAuth))
//            .body(request)
//            .post("/api/v1/accounts/transfer") // эндпоинт
//            .then()
//            .statusCode(HttpStatus.SC_BAD_REQUEST)
//            .body(Matchers.containsString("Invalid transfer: insufficient funds or invalid accounts"));
//}

////@Test
////public void verifyTransferMoneyToNonExistingAccountTest() {
////    CreateUserRequest createRequest = createRandomUser();
////    String userAuth = createAndLoginUser(createRequest);

////    int senderAccountId = createAccount(userAuth);
////    depositMoney(userAuth, senderAccountId, 5000.00);

////    TransferRequest request = TransferRequest.builder()
////            .senderAccountId(senderAccountId)
////            .receiverAccountId(999999)
////            .amount(250.75)
////            .build();

////    new TransferMoneyRequester(
////            RequestSpecs.authWithToken(userAuth),
////            ResponseSpecs.requestReturnsBadRequest("", "Invalid transfer: insufficient funds or invalid accounts")
////    ).post(request);

////    new TransferMoneyRequester(
////            RequestSpecs.authWithToken(userAuth),
////            ResponseSpecs.requestReturnsOK()
////    )
////            .getTransactions(senderAccountId)
////            .body("relatedAccountId", Matchers.not(Matchers.hasItem(999999)));
////}

//@Test
//public void verifyTransferMoneyToNonExistingAccountTest() {
//    CreateUserRequest createRequest = createRandomUser();
//    String userAuth = createAndLoginUser(createRequest);

//    int senderAccountId = createAccount(userAuth);
//    depositMoney(userAuth, senderAccountId, 5000.00);

//    TransferRequest request = TransferRequest.builder()
//            .senderAccountId(senderAccountId)
//            .receiverAccountId(999999)
//            .amount(250.75)
//            .build();

//    // Отправка перевода и проверка ошибки
//    RestAssured
//            .given(RequestSpecs.authWithToken(userAuth))
//            .body(request)
//            .post("/api/v1/accounts/transfer")
//            .then()
//            .statusCode(HttpStatus.SC_BAD_REQUEST)
//            .body(Matchers.containsString("Invalid transfer: insufficient funds or invalid accounts"));

//    // Проверка, что транзакции не включают несуществующий аккаунт
//    RestAssured
//            .given(RequestSpecs.authWithToken(userAuth))
//            .get("/api/v1/accounts/{???}/transactions", senderAccountId)
//            .then()
//            .statusCode(HttpStatus.SC_OK)
//            .body("relatedAccountId", Matchers.not(Matchers.hasItem(999999)));
//}


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
                .header("Authorization");
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

        RestAssured
                .given(RequestSpecs.authWithToken(userAuth))
                .body(request)
                .post("/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo("Deposit amount cannot exceed 5000"));
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

        RestAssured
                .given(RequestSpecs.authWithToken(userAuth))
                .body(request)
                .post("/api/v1/accounts/transfer")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

}

