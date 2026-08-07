package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;

import static io.restassured.RestAssured.given;

public class UpdateCustomerProfileRequester extends Request<BaseModel>{


    public UpdateCustomerProfileRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(BaseModel model) {
        return null;
    }

    public ValidatableResponse get(BaseModel model) {

            return given()
                    .spec(requestSpecification)
                    .get("/api/v1/customer/profile")
                    .then()
                    .assertThat()
                    .spec(responseSpecification);
        }
}
