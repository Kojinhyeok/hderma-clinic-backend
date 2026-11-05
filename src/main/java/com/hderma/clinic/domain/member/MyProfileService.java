package com.hderma.clinic.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * 회원 본인 정보 수정.
 *  - 수정 가능: 이름, 이메일, 휴대전화, 생년월일, 비밀번호
 *  - 수정 불가: 아이디, 회원구분(role), 상태(status) — 요청값을 아예 받지 않음
 */
@Service
@RequiredArgsConstructor
public class MyProfileService {

    private final MemberRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member updateProfile(Long memberId, String name, String email, String phone, LocalDate birthDate) {
        Member member = repository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        String cleanName = name == null ? "" : name.trim();
        String cleanPhone = phone == null ? "" : phone.trim();
        String cleanEmail = email == null ? "" : email.trim();

        if (cleanName.isEmpty()) throw new IllegalArgumentException("이름을 입력해 주세요.");
        if (cleanPhone.replaceAll("\\D", "").length() < 10) {
            throw new IllegalArgumentException("휴대전화번호를 정확히 입력해 주세요.");
        }
        if (birthDate == null) throw new IllegalArgumentException("생년월일을 입력해 주세요.");
        if (birthDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("생년월일이 올바르지 않습니다.");

        String newEmail = null;
        if (!cleanEmail.isEmpty()) {
            if (!cleanEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
            }
            boolean usedByOther = repository.findByEmail(cleanEmail)
                .map(other -> !other.getId().equals(memberId)).orElse(false);
            if (usedByOther) throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            newEmail = cleanEmail;
        }

        member.setName(cleanName);
        member.setPhone(cleanPhone);
        member.setBirthDate(birthDate);
        member.setEmail(newEmail);   // 비우면 이메일 삭제(선택 항목)
        repository.save(member);
        return member;
    }

    @Transactional
    public void changePassword(Long memberId, String currentPassword, String newPassword) {
        Member member = repository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        if (currentPassword == null || !passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("새 비밀번호는 8자 이상으로 입력해 주세요.");
        }
        if (newPassword.length() > 64) {
            throw new IllegalArgumentException("새 비밀번호는 64자 이하로 입력해 주세요.");
        }
        member.setPassword(passwordEncoder.encode(newPassword));
        repository.save(member);
    }
}