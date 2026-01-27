package org.example.entity;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
@Table(name = "invest_products", catalog = "mock_invest")
public class MyDataInvestProduct {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prodCode; // 종목코드
    private String prodName; // 종목명

    // [수정] 수량은 소수점 발생 가능성이 있어 BigDecimal 혹은 double 권장되나 기존 int 유지 시 정밀도 주의
    private int holdQty; // 보유 수량

    @Column(precision = 19, scale = 2)
    private BigDecimal evalAmt; // 평가 금액

    @Column(precision = 10, scale = 2) // 수익률 정밀도 조정
    private BigDecimal earningRate; // 수익률

    // --- 명세서 및 자산 계산을 위해 추가된 필드 ---

    private String prodType;        // 종목유형 (주식, 펀드 등)

    @Column(precision = 19, scale = 2)
    private BigDecimal purchaseAmt; // 매입 금액 (수익률 계산 및 원금 파악용)

    private String currencyCode;    // 통화코드 (KRW, USD 등)

    // --- 기존 관계 및 설정 유지 ---
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invest_id")
    private MyDataInvest invest;
}