package com.hderma.clinic.domain.recruitment;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruitments/{recruitmentId}/slots")
@RequiredArgsConstructor
public class RecruitmentSlotController {

    private final RecruitmentSlotService service;

    /** 공개: 신청 가능한 날짜 목록 */
    @GetMapping("/dates")
    public List<LocalDate> dates(@PathVariable Long recruitmentId) {
        return service.listDates(recruitmentId);
    }

    /** 공개: 특정 날짜의 시간대 목록 (마감 여부 포함) */
    @GetMapping
    public List<RecruitmentSlotDto.Response> byDate(
        @PathVariable Long recruitmentId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return service.listByDate(recruitmentId, date);
    }

    /** 관리자: 전체 슬롯 목록 (모든 날짜) */
    @GetMapping("/all")
    public List<RecruitmentSlotDto.Response> all(@PathVariable Long recruitmentId) {
        return service.listAll(recruitmentId);
    }

    /** 관리자: 슬롯 추가 */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
        @PathVariable Long recruitmentId, @RequestBody RecruitmentSlotDto.Request req
    ) {
        return ResponseEntity.ok(Map.of("id", service.create(recruitmentId, req)));
    }

    /** 관리자: 슬롯 삭제 */
    @DeleteMapping("/{slotId}")
    public ResponseEntity<Void> delete(@PathVariable Long recruitmentId, @PathVariable Long slotId) {
        service.delete(slotId);
        return ResponseEntity.ok().build();
    }
}