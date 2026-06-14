package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;

import static io.restassured.RestAssured.given;

public abstract class Request<T extends BaseModel> {

    protected RequestSpecification requestSpecification;
    protected ResponseSpecification responseSpecification;

    public Request(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        this.requestSpecification = requestSpecification;
        this.responseSpecification = responseSpecification;
    }

    public abstract ValidatableResponse post(T model);

    public ValidatableResponse get(String endpoint) {
        return given(requestSpecification)
                .when()
                .get(endpoint)
                .then()
                .spec(responseSpecification);
    }

    public ValidatableResponse put(String endpoint, T model) {
        return given(requestSpecification)
                .body(model)
                .when()
                .put(endpoint)
                .then()
                .spec(responseSpecification);
    }
}
