package com.hderma.clinic.domain.inquiry;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trial-requests")
@RequiredArgsConstructor
public class TrialRequestController {

    private final TrialRequestService service;

    // 사용자용 — 시험의뢰 접수
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody TrialRequestDto.Request req) {
        Long id = service.create(req);
        // id가 null이면 허니팟에 걸린 봇 요청 — 정상 응답인 것처럼 200으로 조용히 처리
        return ResponseEntity.ok(Map.of("id", id != null ? id : 0));
    }

    // 관리자용 — 목록/상세/상태변경/삭제
    @GetMapping
    public List<TrialRequestDto.Response> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public TrialRequestDto.Response detail(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody TrialRequestDto.StatusUpdateRequest req) {
        service.updateStatus(id, req.getStatus());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}