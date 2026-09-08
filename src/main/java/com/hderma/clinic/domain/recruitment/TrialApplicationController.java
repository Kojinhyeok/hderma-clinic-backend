package com.hderma.clinic.domain.recruitment;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trial-applications")
@RequiredArgsConstructor
public class TrialApplicationController {

    private final TrialApplicationService service;

    @PostMapping
    public ResponseEntity<?> apply(@RequestBody TrialApplicationDto.Request req, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("memberId") != null) {
            req.setMemberId((Long) session.getAttribute("memberId"));
        } else {
            req.setMemberId(null); // 비로그인 신청은 회원 연결 없이 접수
        }
        return ResponseEntity.ok(Map.of("id", service.apply(req)));
    }

    @GetMapping("/by-recruitment/{recruitmentId}")
    public List<TrialApplicationDto.Response> byRecruitment(@PathVariable Long recruitmentId) {
        return service.findByRecruitment(recruitmentId);
    }

    @GetMapping("/by-member/{memberId}")
    public List<TrialApplicationDto.Response> byMember(@PathVariable Long memberId) {
        return service.findByMember(memberId);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody TrialApplicationDto.StatusUpdateRequest req) {
        service.updateStatus(id, req.getStatus());
        return ResponseEntity.ok().build();
    }
}