package org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    // 이 주소는 SecurityConfig에서 permitAll을 안 해줬음
    // 토큰 없이는 절대 못 들어옴 (403 에러 발생)
    @GetMapping("/api/test")
    public String test() {
        return "로그인 사용자만 볼 수 있음";
    }
}
