package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.TransferRequest;

import static io.restassured.RestAssured.given;

public class TransferMoneyRequester extends Request<TransferRequest> {

    public TransferMoneyRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(TransferRequest model) {
        return given()
                .spec(requestSpecification)
                .body(model)
                .post("/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    public ValidatableResponse getTransactions(int accountId) {
        return given()
                .spec(requestSpecification)
                .get("/api/v1/accounts/" + accountId + "/transactions")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
