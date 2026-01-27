package org.example.dto.mock;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor // 👈 [필수 1] 깡통 객체를 만들 수 있게 함
@AllArgsConstructor
public class InvestIrpDto {
    @JsonProperty("company_name")
    private String companyName;
    @JsonProperty("account_num") private String accountNum;
    @JsonProperty("is_consent") private boolean isConsent;
    @JsonProperty("prod_name") private String prodName;
    @JsonProperty("irp_type") private String irpType;
    @JsonProperty("eval_amt") private String evalAmt;
    @JsonProperty("inv_principal") private String invPrincipal;
    @JsonProperty("open_date") private String openDate;
    @JsonProperty("exp_date") private String expDate;
    @JsonProperty("currency_code") private String currencyCode;
}