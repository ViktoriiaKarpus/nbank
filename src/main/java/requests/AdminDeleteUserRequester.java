package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.DeleteUserRequest;

import static io.restassured.RestAssured.given;

public class AdminDeleteUserRequester extends Request<DeleteUserRequest>{

    public AdminDeleteUserRequester(RequestSpecification requestSpecification,
                                  ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(DeleteUserRequest model) {
        return null;
    }

    public ValidatableResponse delete(long userId) {

        return given()
                .spec(requestSpecification)
                .pathParam("id", userId)
                .when()
                .delete("/api/v1/admin/users/{id}")
                .then()
                .spec(responseSpecification);
    }
}
