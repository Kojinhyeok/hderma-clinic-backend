package com.hderma.clinic.domain.popup;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PopupController {

    private final PopupService service;

    /** 메인페이지에서 부르는 사용자용 엔드포인트 */
    @GetMapping("/api/popups/active")
    public List<PopupDto.Response> active() {
        return service.findActiveNow();
    }

    /** 관리자 화면용 */
    @GetMapping("/api/popups")
    public List<PopupDto.Response> list() {
        return service.findAll();
    }

    @PostMapping("/api/popups")
    public ResponseEntity<PopupDto.SaveResult> create(@RequestBody PopupDto.Request req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/api/popups/{id}")
    public ResponseEntity<PopupDto.SaveResult> update(@PathVariable Long id, @RequestBody PopupDto.Request req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/api/popups/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}