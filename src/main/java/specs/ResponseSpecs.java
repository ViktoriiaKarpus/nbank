package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class ResponseSpecs {
    public static final String NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY= "Name must contain two words with letters only";
    public static final String DEPOSIT_AMOUNT_CANNOT_EXCEED_5000 = "Deposit amount cannot exceed 5000";
    public static final String INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS= "Invalid transfer: insufficient funds or invalid accounts";
    public static final String TRANSFER_AMOUNT_CANNOT_EXCEED_10000 = "Transfer amount cannot exceed 10000";
    public static final String DEPOSIT_AMOUNT_MUST_BE_AT_LEAST_001 = "Deposit amount must be at least 0.01";
    public static final String USER_WITH_ID_DELETED_SUCCESSFULLY = "User with ID %d deleted successfully.";

    private ResponseSpecs() {
    }

    private static ResponseSpecBuilder defaultResponseBuilder() {
        return new ResponseSpecBuilder();
    }

    public static ResponseSpecification entityWasCreated() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .build();
    }


    public static ResponseSpecification requestReturnsOK() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static ResponseSpecification requestReturnsBadRequest(String errorKey, List<String> errorValues) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(errorKey, Matchers.containsInAnyOrder(errorValues.toArray()))
                .build();
    }

       public static ResponseSpecification requestReturnsUnauthorized() {
           return defaultResponseBuilder()
                   .expectStatusCode(HttpStatus.SC_UNAUTHORIZED)
                   .build();
       }

    public static ResponseSpecification requestReturnsBadRequestWithText(String expectedMessage) {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(Matchers.containsString(expectedMessage))
                .build();
    }

    public static ResponseSpecification userDeletedSuccessfully(long userId) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .expectBody(Matchers.equalTo(USER_WITH_ID_DELETED_SUCCESSFULLY.formatted(userId)))
                .build();
    }
}
