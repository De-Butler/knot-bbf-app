package org.example.config;

import org.example.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration // 설정파일 명시
@EnableWebSecurity // 스프링 서큘리티 기능 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    // 암호화 도구
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 보안 필터 체인 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                // CSRF 보안 끄기
                // CSRF는 해커가 사용자의 브라우저 쿠키를 훔쳐서 공격하는 방식
                .csrf().disable()

                .cors().configurationSource(corsConfigurationSource())
                .and()

                //세션 사용 끄기 because JWT를 사용
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()

                //URL별 권한 관리
                .authorizeRequests()
                // 로그인, 회원가입, 약관 목록 조회는 누구나 가능 (Public)
                .antMatchers("/api/v1/auth/**", "/api/v1/policies/**").permitAll()

                // 나머지는 토큰이 있어야 접근 가능 (Authenticated)
                .antMatchers("/api/v1/**").authenticated()
                    .anyRequest().authenticated()
                .and()


                // JWT 필터를 Password 필터보다 앞에 삽입
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 프론트엔드에서 보내는 모든 출처를 허용 (보안상 * 보다는 프론트 도메인을 넣는게 좋지만, 시연용이니 * 추천)
        configuration.addAllowedOriginPattern("*");

        // 모든 HTTP 메서드 허용 (GET, POST, PUT, DELETE, OPTIONS 등)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // 모든 헤더 허용 (Content-Type, Authorization 등)
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 쿠키나 인증 정보 포함 허용
        configuration.setAllowCredentials(true);

        // 브라우저가 응답 헤더를 읽을 수 있도록 허용 (필요한 경우)
        configuration.setExposedHeaders(Arrays.asList("Authorization", "x-api-tran-id"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 적용
        return source;
    }
}
