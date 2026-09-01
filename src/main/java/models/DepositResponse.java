package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@JsonIgnoreProperties(ignoreUnknown = true)

public class DepositResponse extends BaseModel {

    private Integer id;
    private Double balance;
    private List<TransferResponse> transactions;
}
