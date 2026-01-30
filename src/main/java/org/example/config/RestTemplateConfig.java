package org.example.config;

import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        // 타임아웃 설정
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 연결 5초 대기
        factory.setReadTimeout(5000);    // 데이터 읽기 5초 대기
        return new RestTemplate(factory);
    }
}
