package org.example.dto.blockchain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class VirtualAssetDto {

    private String chain;
    private String symbol;
    private int decimals;
    private double balance;

    // 👇 두 줄 콤보 적용!

    @JsonProperty("assetType")
    @JsonAlias("asset_type")
    private String assetType;

    @JsonProperty("assetId")
    @JsonAlias("asset_id")
    private String assetId;

    @JsonProperty("balanceRaw")
    @JsonAlias("balance_raw")
    private String balanceRaw;

    @JsonProperty("priceUsd")
    @JsonAlias("price_usd")
    private double priceUsd;

    @JsonProperty("priceKrw")
    @JsonAlias("price_krw")
    private double priceKrw;

    @JsonProperty("valueUsd")
    @JsonAlias("value_usd")
    private double valueUsd;

    @JsonProperty("valueKrw")      // 📤 프론트는 valueKrw를 원함!
    @JsonAlias("value_krw")        // 📥 외부 API는 value_krw를 줌
    private double valueKrw;

    @JsonProperty("pricesTs")
    @JsonAlias("prices_ts")
    private String pricesTs;
}