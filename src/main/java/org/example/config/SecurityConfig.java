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
                // CSRF 보안 끄기
                // CSRF는 해커가 사용자의 브라우저 쿠키를 훔쳐서 공격하는 방식
                .csrf().disable()

                //세션 사용 끄기 because JWT를 사용
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()

                //URL별 권한 관리
                .authorizeHttpRequests()
                    // /api/auth/** 로 시작하는 주소(회원가입, 로그인 등)는 누구나 접근 가능(permitAll)
                    .antMatchers("/api/auth/**").permitAll()
                    // 나머지는 인증된 사람만 접근 가능
                    .anyRequest().authenticated()
                .and()


                // JWT 필터를 Password 필터보다 앞에 삽입
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
