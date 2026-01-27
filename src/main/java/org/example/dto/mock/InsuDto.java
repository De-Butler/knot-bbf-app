package org.example.dto.mock;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor // 👈 [필수 1] 깡통 객체를 만들 수 있게 함
@AllArgsConstructor
public class InsuDto {
    @JsonProperty("company_name")
    private String companyName;
    @JsonProperty("insu_num") private String insuNum;
    @JsonProperty("is_consent") private boolean isConsent;
    @JsonProperty("prod_name") private String prodName;
    @JsonProperty("insu_type") private String insuType;
    @JsonProperty("insu_status") private String insuStatus;
    @JsonProperty("is_renewable") private boolean isRenewable;
    @JsonProperty("issue_date") private String issueDate;
    @JsonProperty("exp_date") private String expDate;
    @JsonProperty("face_amt") private String faceAmt;
    @JsonProperty("currency_code") private String currencyCode;
    @JsonProperty("is_variable") private boolean isVariable;
    @JsonProperty("is_universal") private boolean isUniversal;
}