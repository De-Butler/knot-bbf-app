package org.example.controller;


import org.example.dto.LoginRequestDto;
import org.example.dto.SignupResquestDto;
import org.example.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupResquestDto resquestDto) {
        authService.signup(resquestDto);
        return ResponseEntity.ok("회원가입 완료");
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto requestDto) {
        String token = authService.login(requestDto);
        // 로그인 성공시 프론트엔드에 Bearer 토큰값 반환
        return ResponseEntity.ok(token);
    }
}
