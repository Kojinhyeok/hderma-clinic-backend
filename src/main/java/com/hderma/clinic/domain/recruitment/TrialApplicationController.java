package com.hderma.clinic.domain.recruitment;

import com.hderma.clinic.domain.member.MemberDto;
import com.hderma.clinic.domain.member.MemberService;
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
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<?> apply(@RequestBody TrialApplicationDto.Request req, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("memberId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "로그인 후 신청할 수 있습니다."));
        }
        Long memberId = (Long) session.getAttribute("memberId");

        // 신청자 인적사항은 클라이언트 입력을 신뢰하지 않고, 로그인된 회원 정보로 서버에서 직접 채움
        MemberDto.Response member = memberService.findById(memberId);
        req.setMemberId(memberId);
        req.setApplicantName(member.getName());
        req.setApplicantContact(member.getPhone());
        req.setApplicantBirth(member.getBirthDate());

        try {
            return ResponseEntity.ok(Map.of("id", service.apply(req)));
        } catch (IllegalArgumentException e) {
            // 중복 신청 / 정원 마감 / 필수값 누락 등의 메시지를 화면에 그대로 전달
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
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