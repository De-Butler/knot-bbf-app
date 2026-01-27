package org.example.dto.mock;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor // 👈 [필수 1] 깡통 객체를 만들 수 있게 함
@AllArgsConstructor
public class BankIrpDto {
    private String bankName;
    @JsonProperty("prod_name") private String prodName;
    @JsonProperty("account_num") private String accountNum;
    @JsonProperty("is_consent") private boolean isConsent;
    private String seqno;
    @JsonProperty("irp_type") private String irpType;
    @JsonProperty("eval_amt") private String evalAmt;
    @JsonProperty("inv_principal") private String invPrincipal;
    @JsonProperty("fund_num") private String fundNum;
    @JsonProperty("open_date") private String openDate;
    @JsonProperty("exp_date") private String expDate;
    @JsonProperty("is_isa") private boolean isIsa;
}