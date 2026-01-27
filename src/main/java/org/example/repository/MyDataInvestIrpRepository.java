package org.example.repository;

import org.example.entity.MyDataInvestIrp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MyDataInvestIrpRepository extends JpaRepository<MyDataInvestIrp, Long> {
    void deleteByUserId(Long userId); // 동기화 전 기존 데이터 삭제용
    List<MyDataInvestIrp> findByUserId(Long userId); // 포트폴리오 조회용
}