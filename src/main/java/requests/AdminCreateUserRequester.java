package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.CreateUserRequest;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class AdminCreateUserRequester extends Request<CreateUserRequest> {
    public AdminCreateUserRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(CreateUserRequest model) {
       return given()
                .spec(requestSpecification)
                .body(model)
                .post("/api/v1/admin/users")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    public ValidatableResponse getAllUsers() {
        return given()
                .spec(requestSpecification)
                .get("/api/v1/admin/users")
                .then()
                .statusCode(HttpStatus.SC_OK); // можно позже подключить ResponseSpec для OK
    }

    // Новый метод для GET конкретного пользователя по ID
    public ValidatableResponse getUserById(int userId) {
        return given()
                .spec(requestSpecification)
                .get("/api/v1/admin/users/{id}", userId)
                .then()
                .statusCode(HttpStatus.SC_OK); // тоже можно подключить ResponseSpec
    }

}

