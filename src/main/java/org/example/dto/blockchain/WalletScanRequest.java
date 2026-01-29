package org.example.dto.blockchain;

import lombok.Data;
import java.util.List;

@Data
public class WalletScanRequest {
    private String address;
    private String chain; // 프론트에서 "auto"라고 보냄
}