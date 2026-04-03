package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.UpdateCustomerProfileRequest;

import static io.restassured.RestAssured.given;


public class CustomerProfileRequester extends Request<UpdateCustomerProfileRequest>{

    private static final String ENDPOINT = "/api/v1/customer/profile";

    public CustomerProfileRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(UpdateCustomerProfileRequest model) {
       return given(requestSpecification)
               .body(model)
               .when()
               .post(ENDPOINT)
               .then()
               .spec(responseSpecification);
    }

    public ValidatableResponse get(){
        return given(requestSpecification)
                .when()
                .get(ENDPOINT)
                .then()
                .spec(responseSpecification);
    }

    public ValidatableResponse put(UpdateCustomerProfileRequest model){
        return given(requestSpecification)
                .body(model)
                .when()
                .put(ENDPOINT)
                .then()
                .spec(responseSpecification);
    }
}
