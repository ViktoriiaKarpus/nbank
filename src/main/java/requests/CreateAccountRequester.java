package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;

import static io.restassured.RestAssured.given;

public class CreateAccountRequester extends Request<BaseModel>{

   // public CreateAccountRequester(RequestSpecification requestSpecification,
   //                               ResponseSpecification responseSpecification) {
   //     super(requestSpecification, responseSpecification);
   // }

  //  public CreateAccountRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
  //      super(requestSpecification, responseSpecification);
  //  }
//
//
  //  @Override
  //  public ValidatableResponse post() {
  //      return given()
  //              .spec(requestSpecification)
  //              .when()
  //              .post("http://localhost:4111/api/v1/accounts")
  //              .then()
  //              .assertThat()
  //              .spec(responseSpecification);
  //  }

    public CreateAccountRequester(RequestSpecification requestSpecification,
                                  ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    public ValidatableResponse post(BaseModel model) {
        var request = given().spec(requestSpecification);

        if (model != null) {
            request.body(model);
        }

        return request
                .when()
                .post("/api/v1/accounts")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

  // public ValidatableResponse post(CreateAccountRequest model) {
  //     return given()
  //             .spec(requestSpecification)
  //             .body(model)
  //             .post("/api/v1/accounts") // путь к API
  //             .then(); // только then(), без assert
  // }


}
