package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UpdateCustomerProfileResponse extends BaseModel{

    private Customer customer;
    private String message;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Customer {
        private Integer id;
        private String username;
        private String password;
        private String name;
        private String role;
        private List<Object> accounts;
    }
}
