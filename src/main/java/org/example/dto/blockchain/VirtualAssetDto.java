package org.example.dto.blockchain;

import com.fasterxml.jackson.annotation.JsonAlias; // 💡 여기가 변경됨
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class VirtualAssetDto {

    private String chain;       // eth, btc 등
    private String symbol;      // ETH, BTC
    private int decimals;
    private double balance;

    // 👇 여기부터 JsonAlias 적용

    @JsonAlias("asset_type")
    private String assetType;   // native, erc20 등

    @JsonAlias("asset_id")
    private String assetId;

    @JsonAlias("balance_raw")
    private String balanceRaw;

    @JsonAlias("price_usd")
    private double priceUsd;

    @JsonAlias("price_krw")
    private double priceKrw;

    @JsonAlias("value_usd")
    private double valueUsd;

    @JsonAlias("value_krw")
    private double valueKrw;    // 🌟 중요: 이제 프론트에서 valueKrw로 받음!

    @JsonAlias("prices_ts")
    private String pricesTs;
}