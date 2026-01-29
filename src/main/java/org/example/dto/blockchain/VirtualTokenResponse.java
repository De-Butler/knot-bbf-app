package org.example.dto.blockchain;

import com.fasterxml.jackson.annotation.JsonAlias; // 💡 여기가 변경됨 (JsonProperty -> JsonAlias)
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

    // 📥 입력: "asset_count" (외부 API)
    // 📤 출력: "assetCount" (프론트엔드 - 카멜케이스)
    @JsonAlias("asset_count")
    private int assetCount;

    @JsonAlias("total_value_usd")
    private double totalValueUsd;

    @JsonAlias("total_value_krw")
    private double totalValueKrw;

    // 리스트 변수명도 "assets" 그대로 사용 (자동 매핑)
    private List<VirtualAssetDto> assets;
}