package com.hderma.clinic.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 아이디/비밀번호 찾기 전용 조회 리포지토리.
 * (기존 MemberRepository 는 건드리지 않고 같은 Member 엔티티를 별도로 조회)
 */
public interface MemberRecoveryRepository extends JpaRepository<Member, Long> {
    List<Member> findByNameAndStatus(String name, String status);
    Optional<Member> findByUsernameAndStatus(String username, String status);
}