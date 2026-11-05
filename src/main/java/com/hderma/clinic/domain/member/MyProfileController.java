package com.hderma.clinic.domain.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * 로그인한 회원 본인의 정보 수정 API.
 * 항상 세션의 memberId만 사용 → 타인 정보는 수정할 수 없고, 아이디/권한은 요청에 있어도 무시됨.
 * (/api/my/** 는 AdminAuthFilter 대상이 아니므로 일반 회원도 호출 가능)
 */
@RestController
@RequestMapping("/api/my")
@RequiredArgsConstructor
public class MyProfileController {

    private final MyProfileService service;
    private final MemberService memberService;

    @Getter @Setter
    public static class ProfileUpdateRequest {
        private String name;
        private String email;
        private String phone;
        private LocalDate birthDate;
    }

    @Getter @Setter
    public static class PasswordChangeRequest {
        private String currentPassword;
        private String newPassword;
    }

    private Long sessionMemberId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("memberId") == null) return null;
        return (Long) session.getAttribute("memberId");
    }

    private ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateRequest req, HttpServletRequest request) {
        Long memberId = sessionMemberId(request);
        if (memberId == null) return unauthorized();
        try {
            Member updated = service.updateProfile(memberId, req.getName(), req.getEmail(), req.getPhone(), req.getBirthDate());
            return ResponseEntity.ok(memberService.toResponse(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest req, HttpServletRequest request) {
        Long memberId = sessionMemberId(request);
        if (memberId == null) return unauthorized();
        try {
            service.changePassword(memberId, req.getCurrentPassword(), req.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "비밀번호가 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}