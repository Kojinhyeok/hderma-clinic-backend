package com.hderma.clinic.domain.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AccountRecoveryController {

    private static final String SESSION_MEMBER = "pwResetMemberId";
    private static final String SESSION_AT = "pwResetAt";
    private static final long RESET_TTL_MS = 10 * 60 * 1000L; // 본인 확인 후 10분 안에 비밀번호 설정

    private final AccountRecoveryService service;

    @Getter @Setter
    public static class FindIdRequest {
        private String name;
        private String phone;
    }

    @Getter @Setter
    public static class VerifyRequest {
        private String username;
        private String name;
        private String phone;
    }

    @Getter @Setter
    public static class ResetRequest {
        private String newPassword;
    }

    /** 아이디 찾기 */
    @PostMapping("/find-id")
    public ResponseEntity<?> findId(@RequestBody FindIdRequest req, HttpServletRequest request) {
        List<String> usernames = service.findUsernames(req.getName(), req.getPhone(), clientKey(request));
        return ResponseEntity.ok(Map.of("usernames", usernames));
    }

    /** 비밀번호 찾기 1단계: 본인 확인 */
    @PostMapping("/password-reset/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyRequest req, HttpServletRequest request) {
        Long memberId = service.verifyForPasswordReset(
            req.getUsername(), req.getName(), req.getPhone(), clientKey(request));
        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_MEMBER, memberId);
        session.setAttribute(SESSION_AT, System.currentTimeMillis());
        return ResponseEntity.ok(Map.of("verified", true));
    }

    /** 비밀번호 찾기 2단계: 새 비밀번호 설정 */
    @PostMapping("/password-reset/confirm")
    public ResponseEntity<?> confirm(@RequestBody ResetRequest req, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object memberId = session != null ? session.getAttribute(SESSION_MEMBER) : null;
        Object at = session != null ? session.getAttribute(SESSION_AT) : null;

        if (!(memberId instanceof Long) || !(at instanceof Long)
                || System.currentTimeMillis() - (Long) at > RESET_TTL_MS) {
            if (session != null) {
                session.removeAttribute(SESSION_MEMBER);
                session.removeAttribute(SESSION_AT);
            }
            throw new IllegalArgumentException("본인 확인이 만료되었습니다. 처음부터 다시 진행해 주세요.");
        }

        service.resetPassword((Long) memberId, req.getNewPassword());
        session.removeAttribute(SESSION_MEMBER);
        session.removeAttribute(SESSION_AT);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 이 컨트롤러에서 나는 오류는 화면에 그대로 안내할 수 있도록 메시지를 내려줌
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(AccountRecoveryService.TooManyAttemptsException.class)
    public ResponseEntity<?> tooMany(AccountRecoveryService.TooManyAttemptsException e) {
        return ResponseEntity.status(429).body(Map.of("message", e.getMessage()));
    }

    // Nginx 뒤에서는 마지막 X-Forwarded-For 값이 실제 접속자 IP
    private String clientKey(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            String[] parts = xff.split(",");
            return parts[parts.length - 1].trim();
        }
        return request.getRemoteAddr();
    }
}