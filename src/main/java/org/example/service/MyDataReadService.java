package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.CryptoWallet;
import org.example.domain.User;
import org.example.dto.MyDataPortfolioDto;
import org.example.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyDataReadService {

    private final UserRepository userRepository;
    private final MyDataBankRepository bankRepository;
    private final MyDataCardRepository cardRepository;
    private final MyDataInvestRepository investRepository;
    private final MyDataInvestIrpRepository investIrpRepository;
    private final MyDataInsuranceRepository insuranceRepository;
    private final CryptoWalletRepository cryptoWalletRepository;

    public MyDataPortfolioDto getPortfolioByUsername(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        Long userId = user.getId();

        // 합산용 배열 (람다 내부 사용)
        BigDecimal[] totalKrw = {BigDecimal.ZERO};

        // --- 1. 은행 데이터 ---
        List<MyDataPortfolioDto.BankDto> bankList = new ArrayList<>();
        List<MyDataPortfolioDto.BankDto> bankIrpList = new ArrayList<>();

        bankRepository.findByUserId(userId).forEach(e -> {
            String pName = (e.getProdName() != null) ? e.getProdName() : "";
            String upperPName = pName.toUpperCase();

            MyDataPortfolioDto.BankDto dto = MyDataPortfolioDto.BankDto.builder()
                    .bankName(e.getBankName() != null ? e.getBankName() : "정보없음")
                    .accountNum(e.getAccountNum())
                    .prodName(pName)
                    .accountType(e.getAccountType())
                    .balanceAmt(e.getBalanceAmt() != null ? e.getBalanceAmt().toString() : "0")
                    .lastTranDate(e.getLastTranDate())
                    .build();

            // 대출 계좌는 자산 합산에서 제외 (선택 사항)
            if (e.getBalanceAmt() != null && !upperPName.contains("대출") && !upperPName.contains("론")) {
                totalKrw[0] = totalKrw[0].add(e.getBalanceAmt());
            }

            if (upperPName.contains("IRP")) bankIrpList.add(dto);
            else bankList.add(dto);
        });

        // --- 2. 증권 데이터 (일반 + IRP) ---
        List<MyDataPortfolioDto.InvestDto> investList = investRepository.findByUserId(userId).stream()
                .map(e -> {
                    if (e.getTotalEvalAmt() != null) totalKrw[0] = totalKrw[0].add(e.getTotalEvalAmt());

                    // [수정] Null 안전 처리 추가
                    return MyDataPortfolioDto.InvestDto.builder()
                            .companyName(e.getCompanyName())
                            .accountNum(e.getAccountNum())
                            .accountName(e.getProdName())
                            .totalEvalAmt(e.getTotalEvalAmt() != null ? e.getTotalEvalAmt().toString() : "0")
                            .withdrawableAmt(e.getWithdrawableAmt() != null ? e.getWithdrawableAmt().toString() : "0")
                            .build();
                }).collect(Collectors.toList());

        List<MyDataPortfolioDto.InvestDto> investIrpList = investIrpRepository.findByUserId(userId).stream()
                .map(e -> {
                    if (e.getEvalAmt() != null) totalKrw[0] = totalKrw[0].add(e.getEvalAmt());

                    // [수정] Null 안전 처리 추가
                    return MyDataPortfolioDto.InvestDto.builder()
                            .companyName(e.getCompanyName())
                            .accountNum(e.getAccountNum())
                            .accountName(e.getProdName())
                            .totalEvalAmt(e.getEvalAmt() != null ? e.getEvalAmt().toString() : "0")
                            .withdrawableAmt("0")
                            .build();
                }).collect(Collectors.toList());

        // --- 3. 가상자산 합산 로직 ---
        List<MyDataPortfolioDto.CryptoDto> cryptoList = new ArrayList<>();
        BigDecimal[] cryptoTotalUsd = {BigDecimal.ZERO};

        List<CryptoWallet> wallets = cryptoWalletRepository.findByUser(user);

        if (wallets != null && !wallets.isEmpty()) {
            for (CryptoWallet wallet : wallets) {
                // 원화 총자산 합산
                if (wallet.getTotalValueKrw() != null) {
                    totalKrw[0] = totalKrw[0].add(wallet.getTotalValueKrw());
                }
                // 달러 총자산 합산
                if (wallet.getTotalValueUsd() != null) {
                    cryptoTotalUsd[0] = cryptoTotalUsd[0].add(wallet.getTotalValueUsd());
                }

                // 개별 코인 리스트 변환 및 추가
                if (wallet.getAssets() != null) { // Lazy Loading 작동 (@Transactional 존재)
                    for (org.example.domain.CryptoAsset asset : wallet.getAssets()) {
                        cryptoList.add(MyDataPortfolioDto.CryptoDto.builder()
                                .symbol(asset.getSymbol())
                                .chain(asset.getChain())
                                .balance(asset.getBalance() != null ? asset.getBalance().toString() : "0")
                                .valueKrw(asset.getValueKrw() != null ? asset.getValueKrw().toString() : "0")
                                .valueUsd(asset.getValueUsd() != null ? asset.getValueUsd().toString() : "0")
                                .build());
                    }
                }
            }
        }

        // --- 4. 카드 & 보험 ---
        List<MyDataPortfolioDto.CardDto> cardList = cardRepository.findByUserId(userId).stream()
                .map(e -> MyDataPortfolioDto.CardDto.builder()
                        .cardCompanyName(e.getCardCompanyName() != null ? e.getCardCompanyName() : "정보없음")
                        .cardName(e.getCardName())
                        .cardNum(e.getCardNum())
                        .paymentAmt(e.getPaymentAmt() != null ? e.getPaymentAmt().toString() : "0")
                        .build())
                .collect(Collectors.toList());

        List<MyDataPortfolioDto.InsuDto> insuList = insuranceRepository.findByUserId(userId).stream()
                .map(e -> MyDataPortfolioDto.InsuDto.builder()
                        .companyName(e.getCompanyName() != null ? e.getCompanyName() : "정보없음")
                        .prodName(e.getProdName())
                        .paidAmt(e.getPaidAmt() != null ? e.getPaidAmt().toString() : "0")
                        .build())
                .collect(Collectors.toList());

        return MyDataPortfolioDto.builder()
                .bankList(bankList)
                .bankIrpList(bankIrpList)
                .investList(investList)
                .investIrpList(investIrpList)
                .cryptoList(cryptoList)
                .cardList(cardList)
                .insuList(insuList)
                .totalNetWorthKrw(totalKrw[0])
                .totalNetWorthUsd(cryptoTotalUsd[0])
                .build();
    }
}