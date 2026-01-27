package org.example.entity;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
@Table(name = "invest_irps", catalog = "mock_invest")
public class MyDataInvestIrp {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;

    private String companyName;
    private String accountNum;
    private Boolean isConsent;
    private String prodName;
    private String irpType;

    @Column(precision = 19, scale = 2)
    private BigDecimal evalAmt;
    @Column(precision = 19, scale = 2)
    private BigDecimal invPrincipal;

    private String openDate;
    private String expDate;
    private String currencyCode;
}