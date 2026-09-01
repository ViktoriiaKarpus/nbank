package requests.skelethon;

import lombok.*;
import models.*;

@Getter
@AllArgsConstructor
public enum Endpoint {

    ADMIN_USER(
            "/admin/users",
            CreateUserRequest.class,
            CreateUserResponse.class
    ),

    ACCOUNTS(
            "/accounts",
            BaseModel.class,
            CreateAccountResponse.class
    ),

    CUSTOMER_PROFILE(
        "/customer/profile",
        CustomerProfileRequest.class,
        CustomerProfileResponse.class
    ),

    UPDATE_CUSTOMER_PROFILE(
            "/customer/profile",
            UpdateCustomerProfileRequest.class,
            UpdateCustomerProfileResponse.class
    ),

    DEPOSIT(
            "/accounts/deposit",
            DepositRequest.class,
            DepositResponse.class
    ),

    DELETE_USER(
            "/admin/users/{id}",
            DeleteUserRequest.class,
            DeleteUserResponse.class
    ),

    LOGIN(
            "/auth/login",
            LoginUserRequest.class,
            LoginUserResponse.class
    ),

    TRANSFER_MONEY(
            "/accounts/transfer",
            TransferRequest.class,
            TransferResponse.class
    ),

    TRANSACTIONS(
            "/accounts/{accountId}/transactions",
            BaseModel.class,
            TransferResponse.class
    );


    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
