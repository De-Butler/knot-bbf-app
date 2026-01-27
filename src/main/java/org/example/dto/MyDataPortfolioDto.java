package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; // 추가됨
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MyDataPortfolioDto {
    // --- 은행 섹션 분리 ---
    private List<BankDto> bankList;
    private List<BankDto> bankIrpList; // 추가

    // --- 증권 섹션 분리 ---
    private List<InvestDto> investList;
    private List<InvestDto> investIrpList; // 추가

    // --- 나머지 (합산 제외) ---
    private List<CardDto> cardList;
    private List<InsuDto> insuList;

    //
    private List<CryptoDto> cryptoList;

    // 최종 총자산 (은행 + 은행IRP + 증권 + 증권IRP 합계)
    private BigDecimal totalNetWorthKrw;
    private BigDecimal totalNetWorthUsd;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class BankDto {
        private String bankName;
        private String accountNum;
        private String prodName;
        private String accountType;
        private String balanceAmt;
        private String lastTranDate;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CardDto {
        private String cardCompanyName;
        private String cardName;
        private String cardNum;
        private String cardType;
        private String paymentAmt;
        private String issueDate;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class InvestDto {
        private String companyName;
        private String accountNum;
        private String accountName;
        private String totalEvalAmt;
        private String withdrawableAmt;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class InsuDto {
        private String companyName;
        private String prodName;
        private String insuStatus;
        private String paidAmt;
        private String expDate;
    }

    // 3. [추가] 가상자산 상세 DTO (명세서의 KRW, USD 가치 포함)
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CryptoDto {
        private String symbol;
        private String chain;
        private String balance;
        private String valueKrw;
        private String valueUsd;
    }
}