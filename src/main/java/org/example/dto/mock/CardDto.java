package org.example.dto.mock;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CardDto {
    @JsonProperty("card_id") private String cardId;
    @JsonProperty("card_num") private String cardNum;
    @JsonProperty("card_name") private String cardName;
    @JsonProperty("is_consent") private boolean isConsent;
    @JsonProperty("card_type") private String cardType;
    @JsonProperty("card_member") private String cardMember;
    @JsonProperty("annual_fee") private String annualFee;
    @JsonProperty("issue_date") private String issueDate;
    @JsonProperty("is_trans_payable") private boolean isTransPayable;
    @JsonProperty("payment_amt") private String paymentAmt;

    // --- 명세서 규격 일치 및 컨트롤러 오류 해결을 위해 추가 ---
    @JsonProperty("card_company_code") private String cardCompanyCode; // 추가
    @JsonProperty("card_company_name") private String cardCompanyName; // 추가
}