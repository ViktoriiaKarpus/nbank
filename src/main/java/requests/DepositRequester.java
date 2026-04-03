package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.DepositRequest;

import static io.restassured.RestAssured.given;

public class DepositRequester extends Request<DepositRequest>{
    private static final String ENDPOINT_POST = "/api/v1/accounts/deposit";


    public DepositRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(DepositRequest model) {
        return given()
                .spec(requestSpecification)
                .body(model)
                .post(ENDPOINT_POST)
                .then()
                .assertThat()
                .spec(responseSpecification);

    }

    public ValidatableResponse getTransactions(int accountId){

        return given()
                .spec(requestSpecification)
                .get("/api/v1/accounts" + accountId + "transactions")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
