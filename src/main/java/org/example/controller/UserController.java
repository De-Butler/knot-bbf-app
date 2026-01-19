package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.domain.User;
import org.example.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // 요청 예시: POST /api/user/agreement?type=CRYPTO
    @PostMapping("/agreement")
    @Transactional
    public ResponseEntity<String> updateAgreement(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String type) { // type: CRYPTO 또는 MYDATA

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        if ("CRYPTO".equalsIgnoreCase(type)) {
            user.agreeCryptoService(); // 가상자산 약관 동의 처리
        } else if ("MYDATA".equalsIgnoreCase(type)) {
            user.agreeMyDataService(); // 마이데이터 약관 동의 처리
        } else {
            return ResponseEntity.badRequest().body("잘못된 약관 타입입니다.");
        }

        userRepository.save(user);
        return ResponseEntity.ok("약관 동의가 완료되었습니다.");
    }
    //💡 프론트엔드 개발자와 소통할 때 (이렇게 말하세요!)
    //회원가입 할 때:
    //
    //회원가입 API 보낼 때 serviceAgreed: true 꼭 넣어서 보내줘.
    //
    //가상자산/마이데이터 연결 할 때:
    //
    //"만약 유저가 처음 진입해서 약관 동의 팝업에서 확인을 누르면, POST /api/user/agreement?type=CRYPTO (또는 MYDATA)를 먼저 호출해서 동의 상태를 저장해줘."
    //
    //"그 다음에 지갑 주소 입력 API를 호출하면 돼.
}