package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.BankAccount;
import org.example.domain.CryptoWallet;
import org.example.domain.User;
import org.example.dto.AddAssetRequestDto;
import org.example.dto.DashboardResponseDto;
import org.example.repository.BankAccountRepository;
import org.example.repository.CryptoWalletRepository;
import org.example.repository.UserRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final BankAccountRepository bankRepo;
    private final CryptoWalletRepository cryptoRepo;
    private final UserRepository userRepo;


    // CoinGecko API를 사용
    private static final Map<String, String> COIN_ID_MAP = new HashMap<>();
    static {
        COIN_ID_MAP.put("BTC", "bitcoin");
        COIN_ID_MAP.put("ETH", "ethereum");
        COIN_ID_MAP.put("SOL", "solana");
        COIN_ID_MAP.put("XRP", "ripple");
        COIN_ID_MAP.put("MATIC", "matic-network");
    }

    @Transactional(readOnly = true)
    public DashboardResponseDto getDashboard(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        List<BankAccount> banks = bankRepo.findAllByUser(user);
        List<CryptoWallet> cryptos = cryptoRepo.findAllByUser(user);

        long bankSum = banks.stream().mapToLong(BankAccount::getBalance).sum();
        long cryptoSum = cryptos.stream().mapToLong(CryptoWallet::getBalance).sum();

        return DashboardResponseDto.builder()
                .totalBalance(bankSum + cryptoSum)
                .bankTotalBalance(bankSum)
                .cryptoTotalBalance(cryptoSum)
                .bankAccounts(banks)
                .cryptoWallets(cryptos)
                .build();
    }

    @Transactional
    public void addBank(String username, AddAssetRequestDto dto) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        BankAccount account = BankAccount.builder()
                .user(user)
                .bankname(dto.getBankName())
                .accountNumber(dto.getAccountNumber())
                .balance(dto.getBalance())
                .build();
        bankRepo.save(account);
    }

    @Transactional
    public void addCrypto(String username, AddAssetRequestDto dto) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String network = dto.getSymbol().toUpperCase();
        String address = dto.getWalletAddress();

        BigDecimal balance = BigDecimal.ZERO;

        try {
            // ✅ 메서드 안에서 독립적으로 생성 (설정 꼬임 방지)
            RestTemplate rt = new RestTemplate();

            switch (network) {
                case "BTC": balance = getBitcoinBalance(rt, address); break;
                case "ETH": balance = getEthereumBalance(rt, address); break;
                case "SOL": balance = getSolanaBalance(rt, address); break;
                case "XRP": balance = getRippleBalance(rt, address); break;
                case "MATIC": balance = getPolygonBalance(rt, address); break;
                default: throw new IllegalArgumentException("지원하지 않는 네트워크: " + network);
            }
        } catch (Exception e) {
            System.err.println("API 호출 에러 (" + network + "): " + e.getMessage());
            e.printStackTrace();
            balance = BigDecimal.ZERO;
        }

        BigDecimal currentPriceKrw = BigDecimal.ZERO;
        try {
            RestTemplate rt = new RestTemplate();
            currentPriceKrw = fetchCurrentPrice(rt, network);
        } catch (Exception e) {
            System.err.println("시세 조회 실패: " + e.getMessage());
            currentPriceKrw = BigDecimal.ZERO;
        }

        long totalValue = balance.multiply(currentPriceKrw).longValue();

        CryptoWallet wallet = CryptoWallet.builder()
                .user(user)
                .symbol(network)
                .walletAddress(address)
                .quantity(balance.doubleValue())
                .balance(totalValue)
                .build();

        cryptoRepo.save(wallet);
    }

    // ✅ 모든 요청에 적용될 '슈퍼 헤더' (크롬 브라우저 위장)
    private HttpEntity<?> createEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        headers.add("Accept", "application/json");
        return new HttpEntity<>(body, headers);
    }

    // 1. 비트코인 (Blockstream)
    private BigDecimal getBitcoinBalance(RestTemplate rt, String address) {
        String url = "https://blockstream.info/api/address/" + address;

        ResponseEntity<Map> response = rt.exchange(
                url, HttpMethod.GET, createEntity(null), Map.class
        );

        if (response.getBody() == null) return BigDecimal.ZERO;
        Map<String, Object> stats = (Map<String, Object>) response.getBody().get("chain_stats");
        long funded = ((Number) stats.get("funded_txo_sum")).longValue();
        long spent = ((Number) stats.get("spent_txo_sum")).longValue();
        return new BigDecimal(funded - spent).divide(new BigDecimal("100000000"), 8, RoundingMode.HALF_UP);
    }

    // 2. 이더리움 (PublicNode - Sepolia)
    private BigDecimal getEthereumBalance(RestTemplate rt, String address) {
        String url = "https://ethereum.publicnode.com";
        return callEvmRpc(rt, url, address);
    }

    // 3. 폴리곤 (PublicNode - Amoy)
    private BigDecimal getPolygonBalance(RestTemplate rt, String address) {
        String url = "https://polygon-bor.publicnode.com";
        return callEvmRpc(rt, url, address);
    }

    // EVM 공통 호출
    private BigDecimal callEvmRpc(RestTemplate rt, String url, String address) {
        Map<String, Object> body = new HashMap<>();
        body.put("jsonrpc", "2.0");
        body.put("method", "eth_getBalance");
        body.put("params", Arrays.asList(address, "latest"));
        body.put("id", 1);

        ResponseEntity<Map> response = rt.exchange(
                url, HttpMethod.POST, createEntity(body), Map.class
        );

        if (response.getBody() == null || !response.getBody().containsKey("result")) return BigDecimal.ZERO;
        String resultHex = (String) response.getBody().get("result");
        String hexClean = resultHex.substring(2);
        return new BigDecimal(new java.math.BigInteger(hexClean, 16))
                .divide(new BigDecimal("1000000000000000000"), 18, RoundingMode.HALF_UP);
    }

    // 4. 솔라나 (Devnet)
    private BigDecimal getSolanaBalance(RestTemplate rt, String address) {
        String url = "https://api.mainnet-beta.solana.com";

        Map<String, Object> body = new HashMap<>();
        body.put("jsonrpc", "2.0");
        body.put("method", "getBalance");
        body.put("params", Collections.singletonList(address));
        body.put("id", 1);

        ResponseEntity<Map> response = rt.exchange(
                url, HttpMethod.POST, createEntity(body), Map.class
        );

        Map<String, Object> result = (Map<String, Object>) response.getBody().get("result");
        long lamports = ((Number) result.get("value")).longValue();
        return new BigDecimal(lamports).divide(new BigDecimal("1000000000"), 9, RoundingMode.HALF_UP);
    }

    // 5. 리플 (Testnet)
    private BigDecimal getRippleBalance(RestTemplate rt, String address) {
        String url = "https://xrplcluster.com/";

        Map<String, Object> params = new HashMap<>();
        params.put("account", address);
        params.put("strict", true);
        params.put("ledger_index", "validated");

        Map<String, Object> body = new HashMap<>();
        body.put("method", "account_info");
        body.put("params", Collections.singletonList(params));

        ResponseEntity<Map> response = rt.exchange(
                url, HttpMethod.POST, createEntity(body), Map.class
        );

        if (response.getBody() == null || !response.getBody().containsKey("result")) return BigDecimal.ZERO;
        Map<String, Object> resultWrapper = (Map<String, Object>) response.getBody().get("result");
        if (resultWrapper.containsKey("error")) return BigDecimal.ZERO;

        Map<String, Object> accountData = (Map<String, Object>) resultWrapper.get("account_data");
        String dropsStr = (String) accountData.get("Balance");
        return new BigDecimal(dropsStr).divide(new BigDecimal("1000000"), 6, RoundingMode.HALF_UP);
    }

    // CoinGecko API를 찔러서 현재 원화(KRW) 가격을 가져오는 메서드
    private BigDecimal fetchCurrentPrice(RestTemplate rt, String symbol) {
        String coinId = COIN_ID_MAP.get(symbol.toUpperCase());
        if (coinId == null) return BigDecimal.ZERO;

        // coinGecko API url 생성
        String url = "https://api.coingecko.com/api/v3/simple/price?ids=" + coinId + "&vs_currencies=krw";

        // request 보내기
        ResponseEntity<Map> response = rt.exchange(
                url, HttpMethod.GET, createEntity(null), Map.class
        );

        // 응답에서 가격 꺼내기
        if (response.getBody() == null) return BigDecimal.ZERO;
        Map<String, Object> prices = (Map<String, Object>) response.getBody().get(coinId);

        if (prices == null) return BigDecimal.ZERO;
        Object krwPrice = prices.get("krw");

        // 숫자로 변환
        return new BigDecimal(String.valueOf(krwPrice));
    }
}