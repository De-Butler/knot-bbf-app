package org.example.entity;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
@Table(name = "insurances", catalog = "mock_insurance")
public class MyDataInsurance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String companyName;
    private String prodName;
    private String insuType; // 03: 보장성 등

    @Column(precision = 19, scale = 2)
    private BigDecimal paidAmt;

    // --- 명세서 규격 일치를 위해 추가된 필드 ---

    @Column(nullable = false)
    private String insuNum;         // insu_num (증권번호)

    private Boolean isConsent;      // 전송요구 여부
    private String insuStatus;      // insu_status (01: 정상 등)
    private Boolean isRenewable;    // is_renewable (갱신형 여부)
    private String issueDate;       // issue_date (가입일)
    private String expDate;         // exp_date (만기일)

    @Column(precision = 19, scale = 2)
    private BigDecimal faceAmt;     // face_amt (가입금액/보장금액)

    private String currencyCode;    // currency_code (KRW 등)
    private Boolean isVariable;     // is_variable (변액 여부)
    private Boolean isUniversal;    // is_universal (유니버셜 여부)
}