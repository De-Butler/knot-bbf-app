package org.example.entity;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
@Table(name = "bank_irps", catalog = "mock_bank")
public class MyDataBankIrp {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;

    private String prodName;        // prod_name
    private String accountNum;      // account_num
    private Boolean isConsent;      // is_consent
    private String seqno;           // seqno
    private String irpType;         // irp_type (개인형/기업형)

    @Column(precision = 19, scale = 2)
    private BigDecimal evalAmt;     // eval_amt
    @Column(precision = 19, scale = 2)
    private BigDecimal invPrincipal;// inv_principal

    private String fundNum;         // fund_num
    private String openDate;        // open_date
    private String expDate;         // exp_date
    private Boolean isIsa;          // is_isa
}