package com.hderma.clinic.domain.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody MemberDto.RegisterRequest req) {
        Long id = memberService.register(req);
        return ResponseEntity.ok(java.util.Map.of("id", id));
    }

    @PostMapping("/login")
    public ResponseEntity<MemberDto.Response> login(@RequestBody MemberDto.LoginRequest req, HttpServletRequest request) {
        Member member = memberService.authenticate(req.getUsername(), req.getPassword());

        HttpSession session = request.getSession(true);
        session.setAttribute("memberId", member.getId());
        session.setAttribute("role", member.getRole());

        return ResponseEntity.ok(memberService.toResponse(member));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("memberId") == null) {
            return ResponseEntity.status(401).build();
        }
        Long memberId = (Long) session.getAttribute("memberId");
        return ResponseEntity.ok(java.util.Map.of(
            "memberId", memberId,
            "role", session.getAttribute("role")
        ));
    }

        // admin/js/common.js가 이 경로+필드명을 기대하고 있어서 별도로 맞춰줌
    @GetMapping("/current-user")
    public ResponseEntity<?> currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("memberId") == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(java.util.Map.of(
            "memberId", session.getAttribute("memberId"),
            "position", session.getAttribute("role"),
            "name", "관리자"
        ));
    }
}