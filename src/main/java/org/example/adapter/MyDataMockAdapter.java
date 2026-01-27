package org.example.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// WAS쪽에 새로 만든(또는 Mock서버에서 가져온) DTO들을 Import 해야 합니다.
import org.example.dto.mock.BankAcctResponse;
import org.example.dto.mock.BankIrpResponse;
import org.example.dto.mock.CardResponse;
import org.example.dto.mock.InvestAcctResponse;
import org.example.dto.mock.InvestIrpResponse;
import org.example.dto.mock.InsuResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyDataMockAdapter {

    private final RestTemplate restTemplate;
    @Value("${mydata.mock.domain}")
    private String domain;

    @Value("${mydata.mock.version}")
    private String version;

    // 키값에 맞춘 경로 변수들
    @Value("${mydata.mock.api.bank-accounts}")
    private String bankAccountsPath;

    @Value("${mydata.mock.api.bank-irps}")
    private String bankIrpsPath;

    @Value("${mydata.mock.api.card-cards}")
    private String cardPath;

    @Value("${mydata.mock.api.invest-accounts}")
    private String investAccountsPath;

    @Value("${mydata.mock.api.invest-irps}")
    private String investIrpsPath;

    @Value("${mydata.mock.api.insu-contracts}")
    private String insuPath;

    /**
     * [공통 헤더 생성]
     * 마이데이터 표준 API v2.x 필수 헤더를 모두 포함합니다.
     */
    private HttpHeaders createHeaders(String token, String userSearchId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("x-api-tran-id", generateTranId()); // 거래 고유 ID (자동 생성)
        headers.set("x-api-type", "user-search");
        headers.set("x-api-v2-token", token);
        headers.set("x-user-search-id", userSearchId); // 사용자 식별값 (CI)
        headers.set("user-agent", "MyDataApp/1.0");
        headers.set("Content-Type", "application/json; charset=utf-8");
        return headers;
    }

    /**
     * [유틸] 거래 고유 ID 생성 (25자리: 기관코드10 + M + 난수14)
     */
    private String generateTranId() {
        String prefix = "1234567890M"; // 기관코드(가상)
        long randomNum = ThreadLocalRandom.current().nextLong(10000000000000L, 99999999999999L);
        return prefix + randomNum;
    }

    // =================================================================
    // 1. Bank (은행)
    // =================================================================

    // 1-1. 수신계좌 목록 조회
    public BankAcctResponse getBankAccounts(String token, String userSearchId) {
        log.info(">>>> [Adapter 호출] token={}, userSearchId={}", token, userSearchId);

        String url = domain + version + bankAccountsPath;

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(token, userSearchId)),
                BankAcctResponse.class
        ).getBody();
    }

    // 1-2. [New] 개인형 IRP 계좌 목록 조회
    public BankIrpResponse getBankIrps(String token, String userSearchId) {
        String url = domain + version + bankIrpsPath;

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(token, userSearchId)),
                BankIrpResponse.class
        ).getBody();
    }

    // =================================================================
    // 2. Card (카드)
    // =================================================================
    public CardResponse getCards(String token, String userSearchId) {
        String url = domain + version + cardPath;

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(token, userSearchId)),
                CardResponse.class
        ).getBody();
    }

    // =================================================================
    // 3. Invest (금융투자)
    // =================================================================

    // 3-1. 계좌 목록 조회
    public InvestAcctResponse getInvestAccounts(String token, String userSearchId) {
        String url = domain + version + investAccountsPath;

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(token, userSearchId)),
                InvestAcctResponse.class
        ).getBody();
    }

    // 3-2. [투자] 개인형 IRP 계좌 목록 조회
    public InvestIrpResponse getInvestIrps(String token, String userSearchId) {
        String url = domain + version + investIrpsPath;

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(token, userSearchId)),
                InvestIrpResponse.class
        ).getBody();
    }

    // =================================================================
    // 4. Insurance (보험)
    // =================================================================
    public InsuResponse getInsuContracts(String token, String userSearchId) {
        String url = domain + version + insuPath;

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(token, userSearchId)),
                InsuResponse.class
        ).getBody();
    }
}