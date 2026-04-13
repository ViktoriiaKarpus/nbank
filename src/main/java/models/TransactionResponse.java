package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TransactionResponse extends BaseModel {

    private long id;
    private Double amount;
    private String message;
    private long relatedAccountId;
}
