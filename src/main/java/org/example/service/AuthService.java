package org.example.service;

import lombok.Setter;
import net.bytebuddy.asm.Advice;
import org.example.config.JwtAuthenticationFilter;
import org.example.config.JwtTokenProvider;
import org.example.domain.User;
import org.example.dto.SignupResquestDto;
import org.example.dto.LoginRequestDto;
import org.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화
    private final JwtTokenProvider jwtTokenProvider; // 토큰 생성

    // 회원가입 로직
    @Transactional
    public void signup(SignupResquestDto resquestDto) {
        // 아이디 중복 검사
        if (userRepository.existsByUsername(resquestDto.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(resquestDto.getPassword());

        // 유저 객체 생성
        User user = User.builder()
                .username(resquestDto.getUsername())
                .password(encodedPassword)
                .nickname(resquestDto.getNickname())
                .roles(Collections.singletonList("ROLE_USER"))
                .build();
        // DB저장
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public String login(LoginRequestDto requestDto) {

        // 아이디로 유저 서칭
        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 아이디입니다."));

        // 비밀번호 비교
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return  jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
    }
}
