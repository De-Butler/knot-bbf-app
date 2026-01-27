package org.example.entity;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
@Table(name = "accounts", catalog = "mock_bank")
public class MyDataBank {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String bankName;
    private String accountNum;
    private String prodName;

    @Column(precision = 19, scale = 2)
    private BigDecimal balanceAmt;

    // --- 명세서 규격에 맞추기 위해 추가된 항목들 ---

    private Boolean isConsent;      // 전송요구 여부
    private String seqno;           // 회차
    private Boolean foreignDeposit; // 외화예금여부
    private String accountType;     // 계좌유형 (1001 등)
    private String accountStatus;   // 계좌상태 (01 등)
    private String curPre;          // 통화코드 (KRW 등)

    @Column(precision = 19, scale = 2)
    private BigDecimal withdrawableAmt; // 출금가능액

    @Column(precision = 5, scale = 2)
    private BigDecimal offeredRate;  // 금리

    private String lastTranDate;    // 최종거래일
    private Boolean isInvest;       // 투자상품여부
}