package com.hderma.clinic.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long register(MemberDto.RegisterRequest req) {
        if (req.getUsername() == null || req.getUsername().isBlank()) {
            throw new IllegalArgumentException("아이디를 입력해주세요.");
        }
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }
        if (repository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (req.getEmail() != null && repository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (req.getPrivacyAgreed() == null || !req.getPrivacyAgreed()) {
            throw new IllegalArgumentException("개인정보 수집 및 이용에 동의해주세요.");
        }

        // role은 절대 요청값을 받지 않음 — 가입 시 항상 MEMBER로 고정.
        // PROFESSOR/ADMIN 승격은 관리자 화면에서만 가능 (별도 API).
        Member entity = Member.builder()
            .username(req.getUsername())
            .password(passwordEncoder.encode(req.getPassword()))
            .name(req.getName())
            .email(req.getEmail())
            .phone(req.getPhone())
            .privacyAgreedAt(LocalDateTime.now())
            .build();
        repository.save(entity);
        return entity.getId();
    }

    public Member authenticate(String username, String rawPassword) {
        Member member = repository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!"ACTIVE".equals(member.getStatus())) {
            throw new IllegalArgumentException("탈퇴했거나 비활성화된 계정입니다.");
        }
        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        return member;
    }

    public List<MemberDto.Response> findAll() {
        return repository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void updateRole(Long id, String role) {
        Member member = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다: " + id));
        if (!List.of("MEMBER", "PROFESSOR", "ADMIN").contains(role)) {
            throw new IllegalArgumentException("올바르지 않은 권한 값입니다.");
        }
        member.setRole(role);
    }

    @Transactional
    public void withdraw(Long id) {
        Member member = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다: " + id));
        member.setStatus("WITHDRAWN");
    }

    public MemberDto.Response toResponse(Member e) {
        return MemberDto.Response.builder()
            .id(e.getId()).username(e.getUsername()).name(e.getName())
            .email(e.getEmail()).phone(e.getPhone()).role(e.getRole())
            .status(e.getStatus()).createdAt(e.getCreatedAt())
            .build();
    }
}