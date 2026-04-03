package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.CreateUserRequest;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class AdminCreateUserRequester  extends Request<CreateUserRequest>{

    private static final String ENDPOINT = "/api/v1/admin/users";

    public AdminCreateUserRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(CreateUserRequest model) {
        return given()
                .spec(requestSpecification)
                .body(model)
                .post(ENDPOINT)
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    public ValidatableResponse getAllUsers(){
        return given()
                .spec(requestSpecification)
                .get(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }
}
