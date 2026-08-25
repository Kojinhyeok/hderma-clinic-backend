package com.hderma.clinic.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberAdminController {

    private final MemberService memberService;

    @GetMapping
    public List<MemberDto.Response> list() {
        return memberService.findAll();
    }

    // 교수님/관리자 승격은 오직 이 엔드포인트로만 — 회원가입 화면에는 이 값을 받는 필드 자체가 없음
    @PatchMapping("/{id}/role")
    public ResponseEntity<Void> updateRole(@PathVariable Long id, @RequestBody MemberDto.RoleUpdateRequest req) {
        memberService.updateRole(id, req.getRole());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdraw(@PathVariable Long id) {
        memberService.withdraw(id);
        return ResponseEntity.ok().build();
    }
}