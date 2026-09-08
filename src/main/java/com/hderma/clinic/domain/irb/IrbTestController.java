package com.hderma.clinic.domain.irb;

import com.hderma.clinic.domain.member.Member;
import com.hderma.clinic.domain.member.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/irbs")
@RequiredArgsConstructor
public class IrbTestController {

    private final IrbTestService irbTestService;
    private final MemberRepository memberRepository;

    @GetMapping
    public ResponseEntity<List<IrbDto.TestResponse>> getAllIrb() {
        return ResponseEntity.ok(irbTestService.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<IrbDto.TestResponse>> getActiveIrb() {
        return ResponseEntity.ok(irbTestService.findIsNotTempAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIrbDetail(@PathVariable Long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("memberId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        String role = (String) session.getAttribute("role");
        boolean isAdmin = "ADMIN".equals(role);
        String email = null;

        if (!isAdmin) {
            Long memberId = (Long) session.getAttribute("memberId");
            Member member = memberRepository.findById(memberId).orElse(null);
            if (member == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            email = member.getEmail();
        }

        try {
            return ResponseEntity.ok(irbTestService.findDetail(id, email, isAdmin));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<IrbDto.TestResponse> createIrb(@RequestBody IrbDto.TestRequest req) {
        return ResponseEntity.ok(irbTestService.write(req));
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<IrbDto.TestResponse> createReply(@PathVariable Long id, @RequestBody IrbDto.TestRequest req) {
        return ResponseEntity.ok(irbTestService.createReply(id, req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IrbDto.TestResponse> updateIrb(@PathVariable Long id, @RequestBody IrbDto.TestRequest req) {
        return ResponseEntity.ok(irbTestService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIrb(@PathVariable Long id) {
        irbTestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}