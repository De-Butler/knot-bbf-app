package org.example.config;

import org.example.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// OncePerRequestFilter: 한 요청당 딱 한 번만 검사
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 토큰 추출
        String token = jwtTokenProvider.resolveToken(request);

        // 토큰 O, 유효 O
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰에서 유저 이름 추출
            String username = jwtTokenProvider.getUsername(token);

            // DB에서 유저 정보 가져오기
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 인증되면 허가를 내줌
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

            // SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // 다음으로 넘어가기
        filterChain.doFilter(request, response);
    }
}
