package com.hderma.clinic.domain.recruitment;

import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<Map<String, Object>> apply(@RequestBody TrialApplicationDto.Request req) {
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