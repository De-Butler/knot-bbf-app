package org.example.repository;

import org.example.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 회원가입용: 중복 가입 방지를 위해 존재 여부 확인
    boolean existsByUsername(String username);

    // 로그인용: 아이디로 회원 정보 찾아오기
    // Optional은 null 처리를 안전하게 하기 위해 감싸는 껍데기입니다.
    Optional<User> findByUsername(String username);
}
