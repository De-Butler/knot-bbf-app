package org.example.domain;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "wallet_assets")
public class CryptoAsset {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 지갑의 자산인지 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private CryptoWallet wallet;

    // --- 아래부터 API 명세서 항목 1:1 매핑 ---

    // API 명세: chain (eth, btc, sol...)
    @Column(nullable = false)
    private String chain;

    // API 명세: asset_type (native, erc20...)
    private String assetType;

    // API 명세: asset_id (ETH, 0xContractAddress...)
    private String assetId;

    // API 명세: symbol (ETH, USDT...)
    @Column(nullable = false)
    private String symbol;

    // API 명세: decimals
    private Integer decimals;

    // API 명세: balance_raw (숫자가 너무 커서 String으로 저장 권장)
    private String balanceRaw;

    // API 명세: balance
    @Column(precision = 30, scale = 10)
    private BigDecimal balance;

    // API 명세: price_usd
    @Column(precision = 20, scale = 4)
    private BigDecimal priceUsd;

    // API 명세: price_krw
    @Column(precision = 20, scale = 4)
    private BigDecimal priceKrw;

    // API 명세: value_usd
    @Column(precision = 20, scale = 2)
    private BigDecimal valueUsd;

    // API 명세: value_krw
    @Column(precision = 20, scale = 2)
    private BigDecimal valueKrw;

    // API 명세: prices_ts (가격 기준 시간)
    private LocalDateTime pricesTs;
}