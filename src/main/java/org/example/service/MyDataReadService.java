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

        // 합산용 배열 (람다 내부 사용을 위해 배열 처리)
        BigDecimal[] totalKrw = {BigDecimal.ZERO};
        // --- 1. [하드코딩] 은행 데이터 ---
        List<MyDataPortfolioDto.BankDto> bankList = new ArrayList<>();
        List<MyDataPortfolioDto.BankDto> bankIrpList = new ArrayList<>();

        bankList.add(MyDataPortfolioDto.BankDto.builder()
                .bankName("우리은행").accountNum("1002-123-423123").prodName("입출금통장")
                .accountType("1001").balanceAmt("1500000").lastTranDate("20260125").build());
        totalKrw[0] = totalKrw[0].add(new BigDecimal("1500000"));

        bankIrpList.add(MyDataPortfolioDto.BankDto.builder()
                .bankName("우리은행").accountNum("428-833-7777").prodName("KB 개인형 IRP")
                .accountType("IRP").balanceAmt("12000000").lastTranDate("20220101").build());
        totalKrw[0] = totalKrw[0].add(new BigDecimal("12000000"));


        // --- 2. [하드코딩] 증권 데이터 ---
        List<MyDataPortfolioDto.InvestDto> investList = new ArrayList<>();
        List<MyDataPortfolioDto.InvestDto> investIrpList = new ArrayList<>();

        investList.add(MyDataPortfolioDto.InvestDto.builder()
                .companyName("키움증권").accountNum("555-88-231256").accountName("키움 종합매매")
                .totalEvalAmt("15600000").withdrawableAmt("500000").build());
        totalKrw[0] = totalKrw[0].add(new BigDecimal("15600000"));

        investIrpList.add(MyDataPortfolioDto.InvestDto.builder()
                .companyName("키움증권").accountNum("929-17-223112").accountName("키움 개인형 IRP 계좌")
                .totalEvalAmt("25000000").withdrawableAmt("0").build());
        totalKrw[0] = totalKrw[0].add(new BigDecimal("25000000"));

        // 이 아래 코드가 실제 코드이며 위의 금융 자산 코드는 임시 코드로 삭제 예정
        /*// --- 1. 은행 데이터 ---
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
                    return MyDataPortfolioDto.InvestDto.builder()
                            .companyName(e.getCompanyName())
                            .accountNum(e.getAccountNum())
                            .accountName(e.getProdName())
                            .totalEvalAmt(e.getTotalEvalAmt().toString())
                            .withdrawableAmt(e.getWithdrawableAmt().toString())
                            .build();
                }).collect(Collectors.toList());

        List<MyDataPortfolioDto.InvestDto> investIrpList = investIrpRepository.findByUserId(userId).stream()
                .map(e -> {
                    if (e.getEvalAmt() != null) totalKrw[0] = totalKrw[0].add(e.getEvalAmt());
                    return MyDataPortfolioDto.InvestDto.builder()
                            .companyName(e.getCompanyName())
                            .accountNum(e.getAccountNum())
                            .accountName(e.getProdName())
                            .totalEvalAmt(e.getEvalAmt().toString())
                            .withdrawableAmt("0")
                            .build();
                }).collect(Collectors.toList());*/

        // --- 3. 가상자산 합산 로직 (멀티 지갑 대응) ---
        List<MyDataPortfolioDto.CryptoDto> cryptoList = new ArrayList<>();
        BigDecimal[] cryptoTotalUsd = {BigDecimal.ZERO};

        // [핵심 수정] Java 8 문법: var 제거 및 List 처리
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
                if (wallet.getAssets() != null) {
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

        // --- 4. [하드코딩] 카드 & 보험 ---
        List<MyDataPortfolioDto.CardDto> cardList = new ArrayList<>();
        cardList.add(MyDataPortfolioDto.CardDto.builder()
                .cardCompanyName("현대카드").cardName("현대카드 ZERO").cardNum("2342-****-****-5621")
                .paymentAmt("450000").build());

        List<MyDataPortfolioDto.InsuDto> insuList = new ArrayList<>();
        insuList.add(MyDataPortfolioDto.InsuDto.builder()
                .companyName("삼성화재").prodName("삼성화재 실손보험").paidAmt("50000000").build());

        // 위의 코드도 임시용
        /*// --- 4. 카드 & 보험 ---
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
                .collect(Collectors.toList());*/

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