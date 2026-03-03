package iteration2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class TransferMoney {
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
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("id", Matchers.notNullValue())
                .body("balance", Matchers.equalTo(0f));
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
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", Matchers.equalTo(1))
                .body("balance", Matchers.equalTo(5000f));

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
                              "amount": 250.75
                        }
                        """ )
                .when()
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void depositOneMoreTimeFiveThousandPositiveTest() {
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
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", Matchers.equalTo(1))
                .body("balance", Matchers.equalTo(9749.25f));

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
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", Matchers.equalTo(1))
                .body("balance", Matchers.equalTo(10249.25f));

    }

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
                              "amount": 10000
                        }
                        """ )
                .when()
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK);
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
    public void checkTheBalanceOfTheFirstAccount(){
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .when()
                .get("http://localhost:4111/api/v1/accounts/1/transactions")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("id", Matchers.hasItem(1))
                .body("balance", Matchers.notNullValue());

    }

    @Test
    public void checkTheBalanceOfTheSecondAccount(){
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .when()
                .get("http://localhost:4111/api/v1/accounts/1/transactions")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("id", Matchers.hasItem(2))
                .body("balance", Matchers.notNullValue());

    }

}
