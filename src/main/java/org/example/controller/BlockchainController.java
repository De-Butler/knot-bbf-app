package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.Consent; // 💡 추가
import org.example.domain.User;
import org.example.dto.blockchain.VirtualTokenRequest;
import org.example.dto.blockchain.VirtualTokenResponse;
import org.example.dto.blockchain.WalletScanRequest;
import org.example.repository.ConsentRepository; // 💡 추가
import org.example.repository.UserRepository;
import org.example.service.BlockchainService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/blockchain")
@RequiredArgsConstructor
public class BlockchainController {

    private final BlockchainService blockchainService;
    private final UserRepository userRepository;
    private final ConsentRepository consentRepository; // 💡 검문 검색을 위해 필요!

    @PostMapping("/portfolio")
    public ResponseEntity<VirtualTokenResponse> getPortfolio(
            @RequestBody VirtualTokenRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 1. 유저 찾기
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2.[핵심] 가상자산 약관 동의 여부 체크 (검문소)
        // DB에서 이 유저가 'CRYPTO_WALLET_LOOKUP' 약관에 동의했는지 확인
        Consent consent = consentRepository.findByUserAndType(user, "CRYPTO_WALLET_LOOKUP")
                .orElseThrow(() -> new IllegalArgumentException("가상자산 조회 약관에 대한 동의 내역이 없습니다."));

        // 동의 내역은 있지만, agreed가 false인 경우 (동의 철회 등)
        if (!consent.isAgreed()) {
            throw new IllegalArgumentException("가상자산 조회 약관에 동의해야 서비스를 이용할 수 있습니다.");
        }

        log.info("인증 및 약관 동의 완료된 사용자(Email: {})의 요청", user.getEmail());

        // 3. Service 호출 (통과!)
        VirtualTokenResponse response = blockchainService.refreshWallet(
                user,
                request.getAddress(),
                request.getChains()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/scan")
    public ResponseEntity<VirtualTokenResponse> scanWallet(
            @RequestBody WalletScanRequest request // 👈 Body로 받음
    ) {
        String address = request.getAddress();
        String chain = request.getChain();

        // 1. 체인 파라미터 정리
        // 프론트가 "auto"로 보내면 -> 빈 리스트(Collections.emptyList)로 변환
        // (명세서: "chains가 미지정 또는 빈 배열이면 기본 지원 체인 전체를 조회한다")
        List<String> chains = new ArrayList<>();
        if (chain != null && !"auto".equalsIgnoreCase(chain)) {
            chains.add(chain);
        }

        // 2. 서비스 호출 (DB 저장 안 함)
        VirtualTokenResponse result = blockchainService.scanWallet(address, chains);

        return ResponseEntity.ok(result);
    }
}