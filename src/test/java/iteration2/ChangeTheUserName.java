package iteration2;

import io.restassured.http.ContentType;
import iteration2.utils.BaseTest;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class ChangeTheUserName extends BaseTest {

    private final String userAuth = "Basic dXNlcjh0ZXN0OlN0cm9uZ1Bhc3M3NyQ3";
    @Test
    public void adminCanCreateUserTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body("""
                        {
                          "username": "user8test",
                          "password": "StrongPass77$7",
                          "role": "USER"
                        }
                        """)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("username", Matchers.equalTo("user8test"))
                .body("password", Matchers.not(Matchers.equalTo("user8test")))
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
                          "username": "user8test",
                          "password": "StrongPass77$7"
                        }
                        """)
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .header("Authorization");
    }

    @Test
    public void getCustomerProfileTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization",userAuth )
                .when()
                .get("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("username", Matchers.equalTo("user8test"))
                .body("password", Matchers.not(Matchers.equalTo("user8test")))
                .body("role", Matchers.equalTo("USER"))
                .log().all();
    }

    @Test
    public void updateCustomerProfileTest(){
        String requestBody = """
                {
                  "name": "John Smith"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization",userAuth )
                .body(requestBody)
                .when()
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("customer.name", Matchers.equalTo("John Smith"))
                .body("customer.role", Matchers.equalTo("USER"))
                .log().all();

    }

    @Test
    public void updateCustomerProfileByAddingJustOneNameTest(){
        String requestBody = """
                {
                  "name": "John"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization",userAuth )
                .body(requestBody)
                .when()
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo("Name must contain two words with letters only"))
                .log().all();

    }



    @Test
    public void updateCustomerProfileByAddingNumbersInNameTest(){
        String requestBody = """
                {
                  "name": "John12345"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization",userAuth )
                .body(requestBody)
                .when()
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo("Name must contain two words with letters only"));


    }

    @Test
    public void updateCustomerProfileWithEmptyName(){
        String requestBody = """
                {
                  "name": ""
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization",userAuth )
                .body(requestBody)
                .when()
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo("Name must contain two words with letters only"));


    }

    @Test
    public void updateCustomerProfileWithoutAuthorizationTest(){
        String requestBody = """
                {
                  "name": "John12345""
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .log().all();

    }


}
