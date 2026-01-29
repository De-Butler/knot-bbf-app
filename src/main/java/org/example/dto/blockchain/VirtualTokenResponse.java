package org.example.dto.blockchain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class VirtualTokenResponse {

    private String address;
    private List<String> chains;

    // 👇 여기를 보세요! 두 줄 다 씁니다.

    @JsonProperty("assetCount")      // 📤 나갈 때: assetCount
    @JsonAlias("asset_count")        // 📥 들어올 때: asset_count
    private int assetCount;

    @JsonProperty("totalValueUsd")   // 📤 나갈 때: totalValueUsd
    @JsonAlias("total_value_usd")    // 📥 들어올 때: total_value_usd
    private double totalValueUsd;

    @JsonProperty("totalValueKrw")   // 📤 나갈 때: totalValueKrw (이게 핵심!)
    @JsonAlias("total_value_krw")    // 📥 들어올 때: total_value_krw
    private double totalValueKrw;

    private List<VirtualAssetDto> assets;
}