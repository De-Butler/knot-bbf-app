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
import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyDataSyncService {

    private final MyDataMockAdapter adapter;
    private final UserRepository userRepository;
    private final MyDataBankRepository bankRepository;
    private final MyDataCardRepository cardRepository;
    private final MyDataInvestRepository investRepository;
    private final MyDataInsuranceRepository insuranceRepository;
    private final MyDataInvestIrpRepository investIrpRepository;
    private final BlockchainService blockchainService;
    private final CryptoWalletRepository cryptoWalletRepository;

    @Transactional
    public void syncAllAssets(String username, String mockToken, String userSearchId, Map<String, String> cryptoAddresses) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        Long userId = user.getId();

        // 1. 기존 데이터 삭제
        bankRepository.deleteByUserId(userId);
        cardRepository.deleteByUserId(userId);
        investRepository.deleteByUserId(userId);
        investIrpRepository.deleteByUserId(userId);
        insuranceRepository.deleteByUserId(userId);


        log.info(">>> 자산 동기화 시작: {}", username);
        // -------------------------------------------------------------------------
        // 2. [임시] 금융자산 하드데이터 주입 (Mock 서버 대신 WAS가 직접 저장)
        // -------------------------------------------------------------------------

        // (1) 카드 하드데이터
        cardRepository.save(MyDataCard.builder()
                .userId(userId).cardId("card_001").cardNum("2342-****-****-5621")
                .cardName("현대카드 ZERO").cardType("01").cardMember("01")
                .paymentAmt(new BigDecimal("450000"))
                .cardCompanyName("현대카드").issueDate("20230501").build());

        // (2) 은행 일반 계좌
        bankRepository.save(MyDataBank.builder()
                .userId(userId).bankName("우리은행").accountNum("1002-123-423123")
                .prodName("입출금통장").accountType("1001")
                .balanceAmt(new BigDecimal("1500000")).lastTranDate("20260125").build());

        // (3) 은행 IRP 계좌
        bankRepository.save(MyDataBank.builder()
                .userId(userId).bankName("우리은행").accountNum("428-833-7777")
                .prodName("KB 개인형 IRP").accountType("IRP")
                .balanceAmt(new BigDecimal("12000000")).lastTranDate("20220101").build());

        // (4) 증권 일반 계좌
        investRepository.save(MyDataInvest.builder()
                .userId(userId).companyName("키움증권").accountNum("555-88-231256")
                .prodName("키움 종합매매").totalEvalAmt(new BigDecimal("15600000"))
                .withdrawableAmt(new BigDecimal("500000")).issueDate("20240101").build());

        // (5) 증권 IRP 계좌
        investIrpRepository.save(MyDataInvestIrp.builder()
                .userId(userId).companyName("키움증권").accountNum("929-17-223112")
                .prodName("키움 개인형 IRP 계좌").irpType("201").isConsent(true)
                .evalAmt(new BigDecimal("25000000")).invPrincipal(new BigDecimal("20000000"))
                .openDate("20200501").expDate("20300501").currencyCode("KRW").build());

        // (6) 보험 계약
        insuranceRepository.save(MyDataInsurance.builder()
                .userId(userId).companyName("삼성화재").prodName("삼성화재 실손보험")
                .insuStatus("01").paidAmt(new BigDecimal("50000000"))
                .expDate("20801231").insuNum("100-200-30000").build());

        log.info(">>> [TEMP] 금융자산 주입 완료");
        // 여기부터 실제 WAS 처리:: 위 코드는 임시 코드라 제거예정
        /*// 2. Card Sync (이 부분이 핵심입니다)
        // --- Card Sync Section ---
        try {
            CardResponse res = adapter.getCards(mockToken, userSearchId);

            if (res != null && res.getResultList() != null) {
                log.info(">>> [CARD DEBUG] Received count: {}", res.getResultList().size());

                for (CardDto dto : res.getResultList()) {
                    // [에러 방지] paymentAmt 숫자 변환 안전 처리
                    String rawAmt = dto.getPaymentAmt();
                    BigDecimal safeAmt = BigDecimal.ZERO;
                    try {
                        if (rawAmt != null && !rawAmt.isEmpty()) {
                            // 혹시 모를 콤마(,) 제거 후 변환
                            safeAmt = new BigDecimal(rawAmt.replace(",", ""));
                        }
                    } catch (Exception e) {
                        log.error(">>> [CARD DEBUG] Amount parsing error for value: {}", rawAmt);
                    }

                    cardRepository.save(MyDataCard.builder()
                            .userId(userId)
                            .cardId(dto.getCardId())
                            .cardNum(dto.getCardNum())
                            .cardName(dto.getCardName())
                            .cardType(dto.getCardType())
                            .cardMember(dto.getCardMember())
                            .paymentAmt(safeAmt) // 안전하게 변환된 값 사용
                            .cardCompanyName(dto.getCardCompanyName() != null ? dto.getCardCompanyName() : "현대카드")
                            .issueDate(dto.getIssueDate())
                            .build());
                }
                log.info(">>> [CARD DEBUG] Save Success");
            } else {
                log.warn(">>> [CARD DEBUG] Response is NULL or List is Empty");
            }
        } catch (Exception e) {
            // [중요] 에러 메시지를 영어로 출력해서 깨짐 방지
            log.error(">>> [CARD DEBUG] CRITICAL ERROR: {}", e.toString());
        }
        // 2. Bank Sync (일반 계좌)
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
                            .balanceAmt(new BigDecimal(dto.getBalanceAmt()))
                            .lastTranDate(dto.getLastTranDate())
                            .build());
                }
            }
        } catch (Exception e) {
            log.error("Bank Sync Fail", e);
        }

        // [추가] 3. Bank IRP Sync (은행 IRP)
        try {
            BankIrpResponse res = adapter.getBankIrps(mockToken, userSearchId);
            if (res != null && res.getIrpList() != null) {
                for (BankIrpDto dto : res.getIrpList()) {
                    bankRepository.save(MyDataBank.builder()
                            .userId(userId)
                            .bankName(dto.getBankName())
                            .accountNum(dto.getAccountNum())
                            .prodName(dto.getProdName()) // 명세서의 "KB 개인형 IRP" 등 저장
                            .accountType("IRP") // IRP 구분을 위해 명시적으로 지정
                            .balanceAmt(new BigDecimal(dto.getEvalAmt()))
                            .lastTranDate(dto.getOpenDate())
                            .build());
                }
            }
        } catch (Exception e) {
            log.error("Bank IRP Sync Fail", e);
        }


        // 4. Invest Sync (일반 증권)
        try {
            InvestAcctResponse res = adapter.getInvestAccounts(mockToken, userSearchId);
            if (res != null && res.getAccountList() != null) {
                for (InvestAccountDto dto : res.getAccountList()) {
                    investRepository.save(MyDataInvest.builder()
                            .userId(userId)
                            .companyName(dto.getCompanyName()) // 키움증권
                            .accountNum(dto.getAccountNum())
                            .prodName(dto.getAccountName())
                            .totalEvalAmt(new BigDecimal(dto.getEvalAmt()))
                            .withdrawableAmt(new BigDecimal(dto.getWithdrawableAmt()))
                            .issueDate(dto.getIssueDate())
                            .build());
                }
            }
        } catch (Exception e) {
            log.error("Invest Sync Fail", e);
        }

        // 5. Invest IRP Sync 부분
        try {
            InvestIrpResponse res = adapter.getInvestIrps(mockToken, userSearchId);
            if (res != null && res.getIrpList() != null) {
                for (InvestIrpDto dto : res.getIrpList()) {
                    investIrpRepository.save(MyDataInvestIrp.builder()
                            .userId(userId)
                            .companyName(dto.getCompanyName()) // 이제 명시적으로 호출 가능!
                            .accountNum(dto.getAccountNum())
                            .isConsent(dto.isConsent())
                            .prodName(dto.getProdName())
                            .irpType(dto.getIrpType())
                            .evalAmt(new BigDecimal(dto.getEvalAmt()))
                            .invPrincipal(new BigDecimal(dto.getInvPrincipal()))
                            .openDate(dto.getOpenDate())
                            .expDate(dto.getExpDate())
                            .currencyCode(dto.getCurrencyCode())
                            .build());
                }
            }
        } catch (Exception e) {
            log.error("Invest IRP Sync Fail: {}", e.getMessage());
        }


        // 5. Insurance Sync
        try {
            InsuResponse res = adapter.getInsuContracts(mockToken, userSearchId);
            if (res != null && res.getInsuList() != null) {
                for (InsuDto dto : res.getInsuList()) {
                    insuranceRepository.save(MyDataInsurance.builder()
                            .userId(userId)
                            .companyName(dto.getCompanyName())
                            .prodName(dto.getProdName())
                            .insuStatus(dto.getInsuStatus())
                            .paidAmt(new BigDecimal(dto.getFaceAmt()))
                            .expDate(dto.getExpDate())
                            .insuNum(dto.getInsuNum())
                            .build());
                }
            }
        } catch (Exception e) {
            log.error("Insu Sync Fail", e);
        }*/


        // 6. 가상자산 (Crypto) Sync - Repository 메서드에 최적화
        if (cryptoAddresses != null && !cryptoAddresses.isEmpty()) {
            log.info(">>> [CRYPTO DEBUG] 가상자산 멀티체인 동기화 시작");

            cryptoAddresses.forEach((chainCode, address) -> {
                if (address != null && !address.isEmpty()) {
                    try {
                        log.info(">>> [CRYPTO] 체인: {}, 주소: {} 동기화 시작 (기존 데이터 초기화)", chainCode, address);

                        // 1. [핵심] Repository에 정의된 메서드로 해당 주소 데이터만 삭제
                        // 이렇게 하면 다른 지갑 주소 데이터는 건드리지 않고, 현재 동기화하려는 주소만 깔끔하게 비웁니다.
                        cryptoWalletRepository.deleteByAddressAndUser(address, user);

                        // 2. 삭제 직후 즉시 반영 (혹시 모를 중복 에러 방지용)
                        cryptoWalletRepository.flush();

                        // 3. 무조건 새로고침 호출 (이미 지웠으므로 exists 체크 없이 바로 진행)
                        blockchainService.refreshWallet(
                                user,
                                address,
                                Collections.singletonList(chainCode.toLowerCase())
                        );
                        log.info(">>> [CRYPTO] {} 주소 동기화 완료", address);

                    } catch (Exception e) {
                        log.error(">>> [CRYPTO] {} 동기화 실패: {}", chainCode, e.getMessage());
                    }
                }
            });
        }
    }
}