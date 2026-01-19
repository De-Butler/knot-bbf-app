package org.example.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id // 아래와 세트
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String username; // 아이디

    @Column(nullable = false, length = 100)
    private String password; // 비번

    @Column(nullable = false, length = 50)
    private String nickname; // 별명

    // @ElementCollection: 별도의 테이블로 생성해서 관리해줌 (1:N 관계)
    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();


    // 회원 가입 서비스 이용약관 동의 일시
    private LocalDateTime serviceAgreeAt;

    // 가상자산 조회 서비스 약관 동의 일시
    private LocalDateTime cryptoAgreeAt;

    // 마이데이터 연동 약관 동의 일시
    private LocalDateTime myDataAgreeAt;

    // 동의 여부 체크 편의 메서드
    public boolean isCryptoAgreeAt() {
        return this.cryptoAgreeAt != null;
    }

    public void agreeCryptoService() {
        this.cryptoAgreeAt = LocalDateTime.now();
    }

    public void agreeMyDataService() {
        this.myDataAgreeAt = LocalDateTime.now();
    }
}
