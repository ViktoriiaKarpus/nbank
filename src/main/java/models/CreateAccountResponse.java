package models;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder


public class CreateAccountResponse extends BaseModel {

    private long id;
    private String accountNumber;
    private Double balance;
    private List<Object> transactions;
}
