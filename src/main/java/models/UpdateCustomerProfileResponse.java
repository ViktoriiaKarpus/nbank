package models;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UpdateCustomerProfileResponse extends BaseModel{

    private Customer customer;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Customer {
        private String name;
        private String role;
    }
}
