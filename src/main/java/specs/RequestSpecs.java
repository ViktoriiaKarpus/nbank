package specs;

import configs.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import models.LoginUserRequest;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;

import java.util.List;

public class RequestSpecs {
    private RequestSpecs() {}

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String ADMIN_AUTH_HEADER = "Basic YWRtaW46YWRtaW4=";

    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(
                        new RequestLoggingFilter(),
                        new ResponseLoggingFilter()
                ))
                .setBaseUri(Config.getProperty("server") + Config.getProperty("apiVersion"));
    }

    public static RequestSpecification unauthSpec() {
        return defaultRequestBuilder().build();
    }

    public static RequestSpecification adminSpec() {
        return defaultRequestBuilder()
                .addHeader(AUTHORIZATION_HEADER, ADMIN_AUTH_HEADER)
                .build();
    }

    public static RequestSpecification authAsUser(String username, String password) {
        String userAuthHeader = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK()
        )
                .post(LoginUserRequest.builder()
                        .username(username)
                        .password(password)
                        .build())
                .extract()
                .header(AUTHORIZATION_HEADER);

        return defaultRequestBuilder()
                .addHeader(AUTHORIZATION_HEADER, userAuthHeader)
                .build();
    }

    public static RequestSpecification authWithToken(String token) {
        return defaultRequestBuilder()
                .addHeader(AUTHORIZATION_HEADER, token)
                .build();
    }
}
