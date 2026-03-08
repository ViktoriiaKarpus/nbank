package iteration2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

public class  TransferMoney {
    private final String userAuth = "Basic dXNlcjJ0ZXN0OlN0cm9uZ1Bhc3M3NyQ3";

    @Test
    public void adminCanCreateUserTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body("""
                        {
                          "username": "user2test",
                          "password": "StrongPass77$7",
                          "role": "USER"
                        }
                        """)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("username", Matchers.equalTo("user2test"))
                .body("password", Matchers.not(Matchers.equalTo("user2test")))
                .body("role", Matchers.equalTo("USER"));
    }

    @Test
    public void userCanLoginWithValidDataTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body("""
                        {
                          "username": "user2test",
                          "password": "StrongPass77$7"
                        }
                        """)
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .header("Authorization", Matchers.notNullValue());
    }

    @Test
    public void createTheFirstAccountTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .when()
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("id", Matchers.notNullValue())
                .body("balance", Matchers.equalTo(0f));
    }

    @Test
    public void createTheSecondAccountTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .when()
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("id", Matchers.notNullValue())
                .body("balance", Matchers.equalTo(0f));
    }


    @Test
    public void depositFiveTest(){
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .body("""
                        {
                          "id": 1,
                          "balance": 5000
                        }
                        """)
                .when()
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", Matchers.equalTo(1))
                .body("balance", Matchers.equalTo(5000f));

    }

    @Test
    public void verifyAccountTransactionsAfterDepositingFiveThousand() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .when()
                .get("http://localhost:4111/api/v1/accounts/1/transactions")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", Matchers.hasItem(1))
                .body("amount", Matchers.hasItem(5000f))
                .body("type", Matchers.hasItem("DEPOSIT"))
                .body("relatedAccountId", Matchers.hasItem(1));

    }


    @Test
    public void depositOneMoreTimeFiveThousandTest(){ /// тут правильно
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .body("""
                        {
                          "id": 1,
                          "balance": 5000
                        }
                        """)
                .when()
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", Matchers.equalTo(1))
                .body("balance", Matchers.equalTo(10000.0f));

    }

    @Test
    public void verifyAccountTransactionsAfterDepositingOneMoreTimeFiveThousandTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .when()
                .get("http://localhost:4111/api/v1/accounts/1/transactions")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", Matchers.hasItems(1, 2))
                .body("amount", Matchers.hasItems(5000f, 5000f))
                .body("type", Matchers.everyItem(Matchers.equalTo("DEPOSIT")))
                .body("relatedAccountId", Matchers.everyItem(Matchers.equalTo(1)));

    }

    @Test
    public void transferMoneyFromTheFirstAccountToTheSecondAccountTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .body("""
                         {
                             "senderAccountId": 1,
                             "receiverAccountId": 2,
                              "amount": 9999.99
                        }
                        """ )
                .when()
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .body(Matchers.containsString("Transfer successful"));

    }

    @Test
    public void verifyAccountTransactionAfterTransfer() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .when()
                .get("http://localhost:4111/api/v1/accounts/1/transactions")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", Matchers.hasItem(1));

    }

    @Test
    public void depositOneMoreTimeFiveThousandPositiveTest() {///////////////
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .body("""
                        {
                          "id": 1,
                          "balance": 5000
                        }
                        """)
                .when()
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", Matchers.equalTo(1))
                .body("balance", Matchers.equalTo(5000.01f));

    }

    @Test
    public void verifyAccountTransactionsAfterDepositing_Id1() {//////
        List<String> types =
                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .header("Authorization", userAuth)
                        .when()
                        .get("http://localhost:4111/api/v1/accounts/1/transactions")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .jsonPath().getList("type", String.class);

        long depositCount = types.stream().filter(t -> t.equals("DEPOSIT")).count();
        Assertions.assertEquals(3, depositCount);

    }

    @Test
    public void depositFiveHundredPositiveTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .body("""
                        {
                          "id": 1,
                          "balance": 500
                        }
                        """)
                .when()
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", Matchers.equalTo(1))
                .body("balance", Matchers.equalTo(5500.01F));

    }

    @Test
    public void depositFiveThousandPositiveTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .body("""
                        {
                          "id": 1,
                          "balance": 5000
                        }
                        """)
                .when()
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", Matchers.equalTo(1))
                .body("balance", Matchers.equalTo(10500.01F));

    }


    @Test
    public void verifyAccountTransactionsAfterDepositingFiveHundred() {
        List<String> types =
                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .header("Authorization", userAuth)
                        .when()
                        .get("http://localhost:4111/api/v1/accounts/1/transactions")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .jsonPath().getList("type", String.class);

        long depositCount = types.stream().filter(t -> t.equals("DEPOSIT")).count();
        Assertions.assertEquals(5, depositCount);
    }


    //Invalid transfer: insufficient funds or invalid accounts
    @Test
    public void transferMoneyFromTheFirstAccountToTheSecondAccountMaxValueTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .body("""
                         {
                             "senderAccountId": 1,
                             "receiverAccountId": 2,
                              "amount": 10000.00
                        }
                        """ )
                .when()
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void verifyAccountTransactionsAfterDepositing() {//////
        List<String> types =
                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .header("Authorization", userAuth)
                        .when()
                        .get("http://localhost:4111/api/v1/accounts/1/transactions")
                        .then()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .jsonPath().getList("type", String.class);

        long depositCount = types.stream().filter(t -> t.equals("DEPOSIT")).count();
        Assertions.assertEquals(6, depositCount);

    }

    @Test
    public void transferMoneyFromTheFirstAccountToTheSecondAccount_10000_01() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .body("""
                         {
                             "senderAccountId": 1,
                             "receiverAccountId": 2,
                              "amount": 10000.01
                        }
                        """ )
                .when()
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Transfer amount cannot exceed 10000"));
    }



    @Test
    public void transferMoneyToNonExistingAccountTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .body("""
                         {
                             "senderAccountId": 1,
                             "receiverAccountId": 999,
                              "amount": 250.75
                        }
                        """ )
                .when()
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo("Invalid transfer: insufficient funds or invalid accounts"));
    }

    @Test
    public void verifyTransferMoneyToNonExistingAccountTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .when()
                .get("http://localhost:4111/api/v1/accounts/1/transactions")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("relatedAccountId", Matchers.not(Matchers.hasItem(999)));
    }

}
