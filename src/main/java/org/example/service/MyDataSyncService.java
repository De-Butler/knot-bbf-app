package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.adapter.MyDataMockAdapter;
import org.example.dto.mock.*;
import org.example.entity.*;
import org.example.repository.*;
import org.example.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyDataSyncService {

    private final MyDataMockAdapter adapter;
    private final UserRepository userRepository;

    // 금융자산 리포지토리
    private final MyDataBankRepository bankRepository;
    private final MyDataCardRepository cardRepository;
    private final MyDataInvestRepository investRepository;
    private final MyDataInsuranceRepository insuranceRepository;
    private final MyDataInvestIrpRepository investIrpRepository;

    // 가상자산 서비스 & 리포지토리
    private final BlockchainService blockchainService;
    private final CryptoWalletRepository cryptoWalletRepository;

    @Transactional
    public void syncAllAssets(String username, String mockToken, String userSearchId, Map<String, String> cryptoAddresses) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        Long userId = user.getId();

        log.info(">>> [SYNC] 자산 동기화 시작: {}", username);

        // 1. 기존 금융 데이터 삭제 (초기화)
        bankRepository.deleteByUserId(userId);
        cardRepository.deleteByUserId(userId);
        investRepository.deleteByUserId(userId);
        investIrpRepository.deleteByUserId(userId);
        insuranceRepository.deleteByUserId(userId);

        // ---------------------------------------------------------------------
        // 2. 금융자산 연동 (Mock API) - 안전장치 강화
        // ---------------------------------------------------------------------

        // (1) Card Sync
        try {
            CardResponse res = adapter.getCards(mockToken, userSearchId);
            if (res != null && res.getResultList() != null) {
                for (CardDto dto : res.getResultList()) {
                    cardRepository.save(MyDataCard.builder()
                            .userId(userId)
                            .cardId(dto.getCardId())
                            .cardNum(dto.getCardNum())
                            .cardName(dto.getCardName())
                            .cardType(dto.getCardType())
                            .cardMember(dto.getCardMember())
                            .paymentAmt(parseAmount(dto.getPaymentAmt())) // 안전 변환 메서드 사용
                            .cardCompanyName(dto.getCardCompanyName() != null ? dto.getCardCompanyName() : "현대카드")
                            .issueDate(dto.getIssueDate())
                            .build());
                }
                log.info(">>> [금융] 카드 동기화 완료");
            }
        } catch (Exception e) {
            log.error(">>> [금융] 카드 연동 실패 (무시하고 진행): {}", e.toString());
        }

        // (2) Bank Sync (입출금)
        try {
            BankAcctResponse res = adapter.getBankAccounts(mockToken, userSearchId);
            if (res != null && res.getAccountList() != null) {
                for (BankAccountDto dto : res.getAccountList()) {
                    bankRepository.save(MyDataBank.builder()
                            .userId(userId)
                            .bankName(dto.getBankName())
                            .accountNum(dto.getAccountNum())
                            .prodName(dto.getProdName())
                            .accountType(dto.getAccountType())
                            .balanceAmt(parseAmount(dto.getBalanceAmt())) // 안전 변환
                            .lastTranDate(dto.getLastTranDate())
                            .build());
                }
                log.info(">>> [금융] 은행 동기화 완료");
            }
        } catch (Exception e) {
            log.error(">>> [금융] 은행 연동 실패: {}", e.toString());
        }

        // (3) Bank IRP Sync
        try {
            BankIrpResponse res = adapter.getBankIrps(mockToken, userSearchId);
            if (res != null && res.getIrpList() != null) {
                for (BankIrpDto dto : res.getIrpList()) {
                    bankRepository.save(MyDataBank.builder()
                            .userId(userId)
                            .bankName(dto.getBankName())
                            .accountNum(dto.getAccountNum())
                            .prodName(dto.getProdName())
                            .accountType("IRP")
                            .balanceAmt(parseAmount(dto.getEvalAmt())) // 안전 변환
                            .lastTranDate(dto.getOpenDate())
                            .build());
                }
                log.info(">>> [금융] 은행 IRP 동기화 완료");
            }
        } catch (Exception e) {
            log.error(">>> [금융] 은행 IRP 연동 실패: {}", e.toString());
        }

        // (4) Invest Sync (증권)
        try {
            InvestAcctResponse res = adapter.getInvestAccounts(mockToken, userSearchId);
            if (res != null && res.getAccountList() != null) {
                for (InvestAccountDto dto : res.getAccountList()) {
                    investRepository.save(MyDataInvest.builder()
                            .userId(userId)
                            .companyName(dto.getCompanyName())
                            .accountNum(dto.getAccountNum())
                            .prodName(dto.getAccountName())
                            .totalEvalAmt(parseAmount(dto.getEvalAmt())) // 안전 변환
                            .withdrawableAmt(parseAmount(dto.getWithdrawableAmt()))
                            .issueDate(dto.getIssueDate())
                            .build());
                }
                log.info(">>> [금융] 증권 동기화 완료");
            }
        } catch (Exception e) {
            log.error(">>> [금융] 증권 연동 실패: {}", e.toString());
        }

        // (5) Invest IRP Sync
        try {
            InvestIrpResponse res = adapter.getInvestIrps(mockToken, userSearchId);
            if (res != null && res.getIrpList() != null) {
                for (InvestIrpDto dto : res.getIrpList()) {
                    investIrpRepository.save(MyDataInvestIrp.builder()
                            .userId(userId)
                            .companyName(dto.getCompanyName())
                            .accountNum(dto.getAccountNum())
                            .isConsent(dto.isConsent())
                            .prodName(dto.getProdName())
                            .irpType(dto.getIrpType())
                            .evalAmt(parseAmount(dto.getEvalAmt()))
                            .invPrincipal(parseAmount(dto.getInvPrincipal()))
                            .openDate(dto.getOpenDate())
                            .expDate(dto.getExpDate())
                            .currencyCode(dto.getCurrencyCode())
                            .build());
                }
                log.info(">>> [금융] 증권 IRP 동기화 완료");
            }
        } catch (Exception e) {
            log.error(">>> [금융] 증권 IRP 연동 실패: {}", e.getMessage());
        }

        // (6) Insurance Sync
        try {
            InsuResponse res = adapter.getInsuContracts(mockToken, userSearchId);
            if (res != null && res.getInsuList() != null) {
                for (InsuDto dto : res.getInsuList()) {
                    insuranceRepository.save(MyDataInsurance.builder()
                            .userId(userId)
                            .companyName(dto.getCompanyName())
                            .prodName(dto.getProdName())
                            .insuStatus(dto.getInsuStatus())
                            .paidAmt(parseAmount(dto.getFaceAmt())) // 안전 변환
                            .expDate(dto.getExpDate())
                            .insuNum(dto.getInsuNum())
                            .build());
                }
                log.info(">>> [금융] 보험 동기화 완료");
            }
        } catch (Exception e) {
            log.error(">>> [금융] 보험 연동 실패: {}", e.toString());
        }


        // ---------------------------------------------------------------------
        // 3. 가상자산 (Crypto) Sync - [실제 Blockchain API 호출]
        // ---------------------------------------------------------------------
        if (cryptoAddresses != null && !cryptoAddresses.isEmpty()) {
            log.info(">>> [CRYPTO] 가상자산 멀티체인 동기화 시작 (총 {}개)", cryptoAddresses.size());

            cryptoAddresses.forEach((key, address) -> {
                if (address != null && !address.isEmpty()) {
                    try {
                        log.info(">>> [CRYPTO] 요청 Key: {}, Address: {}", key, address);

                        List<String> targetChains = new ArrayList<>();

                        if (key == null || key.equalsIgnoreCase("auto")) {
                            log.info(">>> [CRYPTO] 자동 스캔 모드 진입");
                        } else {
                            targetChains.add(key.toLowerCase());
                        }

                        // [중요] 기존 데이터 삭제 후 서비스 호출
                        // (BlockchainService 내부에서도 트랜잭션 처리가 되지만, 확실한 갱신을 위해 유지)
                        cryptoWalletRepository.deleteByAddressAndUser(address, user);
                        cryptoWalletRepository.flush();

                        // BlockchainService 호출 -> 실제 API 조회 -> DB 저장
                        blockchainService.refreshWallet(user, address, targetChains);

                        log.info(">>> [CRYPTO] {} 동기화 성공", address);

                    } catch (Exception e) {
                        log.error(">>> [CRYPTO] {} 동기화 실패: {}", key, e.getMessage());
                    }
                }
            });
        }
    }

    // --- Helper Method: 숫자 변환 안전장치 ---
    private BigDecimal parseAmount(String amount) {
        try {
            if (amount != null && !amount.isEmpty()) {
                // 콤마 제거 후 변환
                return new BigDecimal(amount.replace(",", ""));
            }
        } catch (Exception e) {
            // 변환 실패 시 0 반환 (에러로 멈추는 것보다 0원이 나음)
            return BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }
}