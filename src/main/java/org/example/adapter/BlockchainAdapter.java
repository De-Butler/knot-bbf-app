package org.example.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.blockchain.VirtualTokenRequest;
import org.example.dto.blockchain.VirtualTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlockchainAdapter {

    private final RestTemplate restTemplate;
    @Value("${blockchain.api.domain}")
    private String domain;

    @Value("${blockchain.api.version}")
    private String version;

    // 경로 정보 프로퍼티
    @Value("${blockchain.api.tokens}")
    private String tokensPath;

    @Value("${blockchain.api.key}")
    private String apiKey;

    /**
     * 외부 블록체인 API로 포트폴리오 조회 요청 (POST)
     *
     * @param address 조회할 지갑 주소
     * @param chains 조회할 체인 리스트 (null이면 전체 조회)
     * @return 조회된 코인/토큰 정보
     */
    public VirtualTokenResponse getPortfolio(String address, List<String> chains) {

        // 1. 요청 URL 만들기 (http://localhost:8080/v1/virtual + /tokens)
        String url = domain + version + tokensPath;

        // 2. 헤더(Header) 설정 (명세서 필수 요건!)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);                    // 인증 키
        headers.set("x-api-tran-id", generateTranId());      // 트랜잭션 추적 ID

        // 3. 바디(Body) 설정 (DTO -> JSON 자동 변환)
        VirtualTokenRequest requestBody = VirtualTokenRequest.builder()
                .address(address)
                .chains(chains) // null로 보내면 API가 알아서 전체 조회함
                .build();

        // 4. 요청 엔티티 만들기 (헤더 + 바디 합체)
        HttpEntity<VirtualTokenRequest> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("블록체인 API 요청 시작: URL={}, Address={}", url, address);

            // 5. POST 요청 보내기!
            ResponseEntity<VirtualTokenResponse> response = restTemplate.postForEntity(
                    url,
                    requestEntity,
                    VirtualTokenResponse.class
            );

            log.info("블록체인 API 응답 성공: Status={}", response.getStatusCode());
            return response.getBody();

        } catch (Exception e) {
            log.error("블록체인 API 연동 실패: {}", e.getMessage());
            // 실패 시 빈 껍데기라도 리턴하거나 예외를 던짐 (정책에 따라 선택)
            throw new RuntimeException("블록체인 연동 중 오류 발생", e);
        }
    }

    // 트랜잭션 ID 생성기 (랜덤 UUID 사용)
    private String generateTranId() {
        return "req-" + UUID.randomUUID().toString().substring(0, 8);
    }
}