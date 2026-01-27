package org.example.dto.blockchain;

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

    @JsonProperty("asset_count")
    private int assetCount;
    // 12



    @JsonProperty("total_value_usd")
    private double totalValueUsd;

    @JsonProperty("total_value_krw")
    private double totalValueKrw;

    // 리스트 이름표 붙이기 필수!
    @JsonProperty("assets")
    private List<VirtualAssetDto> assets;
}