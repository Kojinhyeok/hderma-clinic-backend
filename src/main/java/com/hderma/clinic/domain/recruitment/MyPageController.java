package com.hderma.clinic.domain.recruitment;

import com.hderma.clinic.domain.member.MemberDto;
import com.hderma.clinic.domain.member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 로그인한 회원 본인 전용 API (내정보 / 내 신청내역).
 * - 항상 세션의 memberId만 사용 (클라이언트가 보낸 memberId는 받지 않음 → 타인 정보 조회 불가)
 * - /api/my/** 는 AdminAuthFilter 대상이 아니므로 일반 회원도 호출 가능
 */
@RestController
@RequestMapping("/api/my")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;
    private final TrialApplicationService applicationService;

    private Long sessionMemberId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("memberId") == null) return null;
        return (Long) session.getAttribute("memberId");
    }

    private ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(HttpServletRequest request) {
        Long memberId = sessionMemberId(request);
        if (memberId == null) return unauthorized();
        return ResponseEntity.ok(memberService.findById(memberId));
    }

    @GetMapping("/applications")
    public ResponseEntity<?> applications(HttpServletRequest request) {
        Long memberId = sessionMemberId(request);
        if (memberId == null) return unauthorized();
        return ResponseEntity.ok(applicationService.findMine(memberId));
    }

    /** 상세 페이지에서 "이미 신청한 시험인지" 미리 확인용 */
    @GetMapping("/applications/check")
    public ResponseEntity<?> check(@RequestParam Long recruitmentId, HttpServletRequest request) {
        Long memberId = sessionMemberId(request);
        if (memberId == null) return ResponseEntity.ok(Map.of("applied", false));
        return ResponseEntity.ok(Map.of("applied", applicationService.hasActiveApplication(recruitmentId, memberId)));
    }

    @PatchMapping("/applications/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id, HttpServletRequest request) {
        Long memberId = sessionMemberId(request);
        if (memberId == null) return unauthorized();
        try {
            applicationService.cancelMine(id, memberId);
            return ResponseEntity.ok(Map.of("message", "신청이 취소되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}