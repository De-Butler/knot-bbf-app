package org.example.entity;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
@Table(name = "invest_accounts", catalog = "mock_invest")
public class MyDataInvest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String companyName;
    private String accountNum;

    private String prodName;

    @Column(precision = 19, scale = 2)
    private BigDecimal totalEvalAmt; // 총 평가 금액

    // --- 명세서 규격 일치를 위해 추가된 필드 ---

    private Boolean isConsent;      // 전송요구 여부
    private String accountName;     // 계좌명
    private String accountType;     // 계좌유형 (101: 위탁 등)
    private String accountStatus;   // 계좌상태 (01: 활동 등)
    private String issueDate;       // 개설일
    private Boolean isTaxBenefits;  // 세제혜택 여부

    @Column(precision = 19, scale = 2)
    private BigDecimal withdrawableAmt; // 인출가능금액(예수금)

    private String currencyCode;    // 통화코드 (KRW 등)

    // --- 기존 관계 및 메서드 유지 ---
    @Builder.Default
    @OneToMany(mappedBy = "invest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MyDataInvestProduct> products = new ArrayList<>();

    public void addProduct(MyDataInvestProduct product) {
        this.products.add(product);
        product.setInvest(this);
    }
}