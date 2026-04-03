package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.LoginUserRequests;

import static io.restassured.RestAssured.given;

public class LoginUserRequester extends Request<LoginUserRequests>{

    private static final String ENDPOINT = "/api/v1/auth/login";

    public LoginUserRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(LoginUserRequests model) {
        return given()
                .spec(requestSpecification)
                .body(model)
                .post(ENDPOINT)
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
