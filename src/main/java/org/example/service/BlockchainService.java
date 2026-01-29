package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.adapter.BlockchainAdapter;
import org.example.domain.CryptoAsset;
import org.example.domain.CryptoWallet;
import org.example.domain.User;
import org.example.dto.blockchain.VirtualAssetDto;
import org.example.dto.blockchain.VirtualTokenResponse;
import org.example.repository.CryptoWalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockchainService {

    private final BlockchainAdapter blockchainAdapter;
    private final CryptoWalletRepository cryptoWalletRepository;

    /**
     * 블록체인 지갑 연동 및 DB 저장 (새로고침)
     * Java 8 문법 기준 작성
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public VirtualTokenResponse refreshWallet(User user, String address, List<String> chains) {
        log.info("블록체인 지갑 연동 시작 - 사용자: {}, 주소: {}, 체인: {}", user.getId(), address, chains);

        // 1. Adapter 조회
        VirtualTokenResponse response = blockchainAdapter.getPortfolio(address, chains);

        // 2. [중요] 기존 데이터 삭제 후 즉시 반영 (Flush)
        // deleteBy... 는 트랜잭션 종료 시점에 나가는 경우가 많아, 강제로 먼저 비워줘야 합니다.
        cryptoWalletRepository.deleteByAddressAndUser(address, user);
        cryptoWalletRepository.flush(); // 이 한 줄이 데이터를 살릴 수 있습니다.

        // 3. 지갑(Wallet) 엔티티 생성
        CryptoWallet wallet = CryptoWallet.builder()
                .user(user)
                .address(address)
                .assetCount(response.getAssetCount())
                .totalValueUsd(BigDecimal.valueOf(response.getTotalValueUsd()))
                .totalValueKrw(BigDecimal.valueOf(response.getTotalValueKrw()))
                .lastUpdated(java.time.LocalDateTime.now())
                .assets(new ArrayList<>()) // 초기화
                .build();

        // 4. 상세 자산 변환
        if (response.getAssets() != null) {
            for (VirtualAssetDto dto : response.getAssets()) {
                CryptoAsset asset = CryptoAsset.builder()
                        .wallet(wallet) // 연관관계 설정
                        .chain(dto.getChain())
                        .assetType(dto.getAssetType())
                        .assetId(dto.getAssetId())
                        .symbol(dto.getSymbol())
                        .decimals(dto.getDecimals())
                        .balanceRaw(dto.getBalanceRaw())
                        .balance(BigDecimal.valueOf(dto.getBalance()))
                        .priceUsd(BigDecimal.valueOf(dto.getPriceUsd()))
                        .priceKrw(BigDecimal.valueOf(dto.getPriceKrw()))
                        .valueUsd(BigDecimal.valueOf(dto.getValueUsd()))
                        .valueKrw(BigDecimal.valueOf(dto.getValueKrw()))
                        .build();

                wallet.getAssets().add(asset);
            }
        }

        // 5. DB 저장 및 로그 확인
        CryptoWallet savedWallet = cryptoWalletRepository.saveAndFlush(wallet); // 저장 후 즉시 반영
        log.info(">>> [DB 저장 결과] ID: {}, 자산 수: {}", savedWallet.getId(), savedWallet.getAssets().size());

        return response;
    }
    public VirtualTokenResponse scanWallet(String address, List<String> chains) {
        // 로그 정도만 남기고 바로 어댑터 호출
        log.info(">>> [Scan] 지갑 단순 조회 (No Save): {}", address);

        // DB 저장(repository.save) 없이 어댑터 결과만 바로 리턴!
        return blockchainAdapter.getPortfolio(address, chains);
    }
}