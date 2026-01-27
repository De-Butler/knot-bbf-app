package org.example.domain;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "wallets", catalog = "mock_crypto")
public class CryptoWallet {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // API 명세: address
    @Column(nullable = false)
    private String address;

    // API 명세: asset_count
    private Integer assetCount;

    // API 명세: total_value_usd
    @Column(precision = 20, scale = 2)
    private BigDecimal totalValueUsd;

    // API 명세: total_value_krw
    @Column(precision = 20, scale = 2)
    private BigDecimal totalValueKrw;

    // 캐싱용: 마지막 업데이트 시간 (300초 체크용)
    private LocalDateTime lastUpdated;

    // User와의 관계 (우리 서비스 내부용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)) // FK 생성 방지
    private User user;

    // 자산 리스트 (1:N 관계)
    @Builder.Default
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CryptoAsset> assets = new ArrayList<>();
}