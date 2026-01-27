package org.example.entity;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
@Table(name = "cards", catalog = "mock_card") // mock_card DB 사용
public class MyDataCard {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    // --- 기존 필드 유지 ---
    @Column(nullable = false)
    private String cardId;          // card_id (카드 식별자)

    @Column(nullable = false)
    private String cardNum;         // card_num (카드 번호)

    private String cardName;        // card_name (상품명)
    private String cardType;        // card_type (신용/체크 구분)

    @Column(precision = 19, scale = 2)
    private BigDecimal paymentAmt;  // payment_amt (결제 예정 금액)

    private String cardCompanyCode; // card_company_code
    private String cardCompanyName; // card_company_name

    // --- 명세서 규격 일치를 위해 추가된 필드 ---

    private Boolean isConsent;      // 전송요구 여부 (표준 API 공통 항목)
    private String cardMember;      // 본인/가족 구분 (01: 본인 등)
    private String annualFee;       // 연회비
    private String issueDate;       // 발급일 (YYYYMMDD)
    private Boolean isTransPayable; // 교통카드 기능 여부
}