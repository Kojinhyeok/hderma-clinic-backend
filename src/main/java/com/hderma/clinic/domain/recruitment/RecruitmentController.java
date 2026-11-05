package com.hderma.clinic.domain.recruitment;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruitments")
@RequiredArgsConstructor
public class RecruitmentController {

    private final RecruitmentService service;

    /**
     * 모집공고 목록.
     * - 기본: 노출 ON 인 공고만 (홈페이지 방문자용)
     * - includeHidden=true: 노출 OFF 까지 모두 — 관리자(ADMIN) 로그인 상태에서만 적용, 그 외에는 무시
     */
    @GetMapping
    public List<RecruitmentDto.Response> list(@RequestParam(required = false) String evalCategory,
                                              @RequestParam(required = false, defaultValue = "false") boolean includeHidden,
                                              HttpServletRequest request) {
        return service.findAll(evalCategory, includeHidden && isAdmin(request));
    }

    @GetMapping("/{id}")
    public RecruitmentDto.Response detail(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody RecruitmentDto.Request req) {
        return ResponseEntity.ok(Map.of("id", service.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody RecruitmentDto.Request req) {
        service.update(id, req);
        return ResponseEntity.ok().build();
    }

    /** 노출 ON/OFF 변경 (관리자 전용 — AdminAuthFilter 가 PATCH 를 보호함) */
    @PatchMapping("/{id}/visible")
    public ResponseEntity<Void> updateVisible(@PathVariable Long id, @RequestBody RecruitmentDto.VisibleRequest req) {
        if (req.getVisible() == null) throw new IllegalArgumentException("노출 여부(visible) 값이 필요합니다.");
        service.updateVisible(id, req.getVisible());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && "ADMIN".equals(session.getAttribute("role"));
    }
}