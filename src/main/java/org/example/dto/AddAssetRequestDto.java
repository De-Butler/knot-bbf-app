package org.example.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
// 입력용 클래스
public class AddAssetRequestDto {
    // 여기는 은행용
    private String bankName;
    private String accountNumber;

    // 코인용
    private String network;
    private String symbol;
    private String walletAddress;

    // 공통
    private Long balance;
    private Double quantity;
}
