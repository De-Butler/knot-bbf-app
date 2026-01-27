package org.example.dto.mock;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MockAssetResponse<T> {
    private String rspCode;
    private String rspMsg;
    private String searchTimestamp;
    private String nextPage;
    private int resultCount;
    private List<T> resultList;

    // --- 내부 클래스들 (각 도메인 매핑) ---
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class BankAccount {
        private String accountNum;
        private boolean isConsent;
        private String seqno;
        private boolean foreignDeposit;
        private String prodName;
        private String accountType;
        private String accountStatus;
        private String curPre;
        private String balanceAmt;
        private String withdrawableAmt;
        private String offeredRate;
        private String lastTranDate;
        private boolean isInvest;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Card {
        private String cardId;
        private String cardNum;
        private String cardName;
        private boolean isConsent;
        private String cardType;
        private String cardMember;
        private String annualFee;
        private String issueDate;
        private boolean isTransPayable;
        private String paymentAmt;
        private String cardCompanyCode;
        private String cardCompanyName;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SecurityAccount {
        private String accountNum;
        private boolean isConsent;
        private String accountName;
        private String accountType;
        private String accountStatus;
        private String issueDate;
        private boolean isTaxBenefits;
        private String withdrawableAmt;
        private String totalEvalAmt;
        private String currencyCode;
        private List<Product> products;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Product {
        private String prodCode;
        private String prodName;
        private String prodType;
        private String holdQty;
        private String evalAmt;
        private String purchaseAmt;
        private String earningRate;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Insurance {
        private String insuNum;
        private boolean isConsent;
        private String prodName;
        private String insuType;
        private String insuStatus;
        private boolean isRenewable;
        private String issueDate;
        private String expDate;
        private String faceAmt;
        private String paidAmt;
        private String currencyCode;
        private boolean isVariable;
        private boolean isUniversal;
    }
}