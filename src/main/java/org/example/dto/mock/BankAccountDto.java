package org.example.dto.mock;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor // 👈 [필수 1] 깡통 객체를 만들 수 있게 함
@AllArgsConstructor
public class BankAccountDto {
    @JsonProperty("bank_name")
    private String bankName;
    @JsonProperty("account_num") private String accountNum;
    @JsonProperty("is_consent") private boolean isConsent;
    @JsonProperty("seqno")
    private String seqno;
    @JsonProperty("foreign_deposit") private boolean foreignDeposit;
    @JsonProperty("prod_name") private String prodName;
    @JsonProperty("account_type") private String accountType;
    @JsonProperty("account_status") private String accountStatus;
    @JsonProperty("cur_pre") private String curPre;
    @JsonProperty("balance_amt") private String balanceAmt;
    @JsonProperty("withdrawable_amt") private String withdrawableAmt;
    @JsonProperty("offered_rate") private String offeredRate;
    @JsonProperty("last_tran_date") private String lastTranDate;
    @JsonProperty("is_invest") private boolean isInvest;
}