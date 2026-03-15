package it_2;

import io.restassured.http.ContentType;
import it_2.utils.Base_Test;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class Deposit_Money extends Base_Test {
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
    public void verifyThatUserWasCreated() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .get("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
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

    //  @Test
    //  public void depositTwoHundredAndFifty(){
    //      given()
    //              .contentType(ContentType.JSON)
    //              .accept(ContentType.JSON)
    //              .header("Authorization", userAuth)
    //              .body("""
    //                      {
    //                       "id": 1,
    //                        "balance": 250.5
    //                      }
    //                      """)
    //              .when()
    //              .post("http://localhost:4111/api/v1/accounts/deposit")
    //              .then()
    //              .assertThat()
    //              .statusCode(HttpStatus.SC_OK)
    //              .body("id", Matchers.equalTo(1))
    //              .body("balance", Matchers.equalTo(250.5f));
    //  }
//
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
    public void verifyAccountTransactionsAfterDepositingFiveHundred() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .when()
                .get("http://localhost:4111/api/v1/accounts/1/transactions")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", Matchers.hasItem(1))
                .body("amount", Matchers.hasItem(5000.0f))
                .body("type", Matchers.hasItem("DEPOSIT"))
                .body("relatedAccountId", Matchers.hasItem(1));

    }


    @Test
    public void depositWithoutAuthorizationTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
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
                .statusCode(HttpStatus.SC_UNAUTHORIZED);

    }

    @Test
    public void verifyAccountTransactionsAfterWithoutAuthorization() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("http://localhost:4111/api/v1/accounts/1/transactions")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);

    }



    /**
     * Sorry, I understand that there should be two possible error messages here,
     * but to be honest , I am not sure how to implement both of them:
     * "Deposit amount must be at least 0.01"
     * "Deposit amount cannot exceed 5000"
     */


    public static Stream<Arguments> depositInvalidData() {
        return Stream.of(
                Arguments.of(5000.1),
                Arguments.of(-1)
        );
    }

    @ParameterizedTest
    @MethodSource("depositInvalidData")
    void userCannotDepositInvalidAmountTest(double amount) {

        String requestBody = """
                {
                  "id": 1,
                  "balance": %f
                }
                """.formatted(amount);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .body(requestBody)
                .when()
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Bad Request"));
    }

    @Test
    public void verifyTransactionsAfterInvalidDeposit() {

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuth)
                .when()
                .get("http://localhost:4111/api/v1/accounts/1/transactions")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("amount", Matchers.not(Matchers.hasItem(5000.1f)))
                .body("amount", Matchers.not(Matchers.hasItem(-1f)));
    }

}
