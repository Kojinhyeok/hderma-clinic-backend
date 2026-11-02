package com.hderma.clinic.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 아이디 찾기 / 비밀번호 찾기.
 *  - 아이디 찾기  : 이름 + 휴대전화번호가 가입 정보와 일치하면 아이디를 알려줌
 *  - 비밀번호 찾기: 아이디 + 이름 + 휴대전화번호가 일치하면 새 비밀번호 설정 허용
 * 이메일은 가입 시 선택 입력이라, 모든 회원이 가능하도록 이름/휴대전화 기준으로 확인한다.
 * 무작위 대입을 막기 위해 실패 횟수를 제한한다. (관리자 계정은 이 경로로 찾기/재설정 불가)
 */
@Service
@RequiredArgsConstructor
public class AccountRecoveryService {

    private static final int MAX_FAILS = 10;                 // 허용 실패 횟수
    private static final long WINDOW_MS = 10 * 60 * 1000L;   // 10분 안에서 집계

    private final MemberRecoveryRepository repository;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, Deque<Long>> fails = new ConcurrentHashMap<>();

    public static class TooManyAttemptsException extends RuntimeException {
        public TooManyAttemptsException(String message) { super(message); }
    }

    /** 이름 + 휴대전화번호로 아이디 찾기 */
    public List<String> findUsernames(String name, String phone, String clientKey) {
        String cleanName = clean(name);
        String digits = digits(phone);
        if (cleanName.isEmpty()) throw new IllegalArgumentException("이름을 입력해 주세요.");
        if (digits.length() < 10) throw new IllegalArgumentException("휴대전화번호를 정확히 입력해 주세요.");

        String key = "id:" + clientKey;
        assertNotLocked(key);

        List<String> usernames = repository.findByNameAndStatus(cleanName, "ACTIVE").stream()
            .filter(this::isRecoverable)
            .filter(m -> digits.equals(digits(m.getPhone())))
            .map(Member::getUsername)
            .collect(Collectors.toList());

        if (usernames.isEmpty()) {
            recordFail(key);
            throw new IllegalArgumentException("입력하신 정보와 일치하는 회원이 없습니다.");
        }
        fails.remove(key);
        return usernames;
    }

    /** 아이디 + 이름 + 휴대전화번호 본인 확인. 일치하면 회원 id 반환 */
    public Long verifyForPasswordReset(String username, String name, String phone, String clientKey) {
        String cleanUser = clean(username);
        String cleanName = clean(name);
        String digits = digits(phone);
        if (cleanUser.isEmpty()) throw new IllegalArgumentException("아이디를 입력해 주세요.");
        if (cleanName.isEmpty()) throw new IllegalArgumentException("이름을 입력해 주세요.");
        if (digits.length() < 10) throw new IllegalArgumentException("휴대전화번호를 정확히 입력해 주세요.");

        String ipKey = "pw:" + clientKey;
        String userKey = "pwu:" + cleanUser.toLowerCase();
        assertNotLocked(ipKey);
        assertNotLocked(userKey);

        Member member = repository.findByUsernameAndStatus(cleanUser, "ACTIVE")
            .filter(this::isRecoverable)
            .filter(m -> cleanName.equals(clean(m.getName())))
            .filter(m -> digits.equals(digits(m.getPhone())))
            .orElse(null);

        if (member == null) {
            recordFail(ipKey);
            recordFail(userKey);
            throw new IllegalArgumentException("입력하신 정보와 일치하는 회원이 없습니다.");
        }
        fails.remove(ipKey);
        fails.remove(userKey);
        return member.getId();
    }

    /** 본인 확인이 끝난 회원의 비밀번호 변경 */
    @Transactional
    public void resetPassword(Long memberId, String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자 이상으로 입력해 주세요.");
        }
        if (newPassword.length() > 64) {
            throw new IllegalArgumentException("비밀번호는 64자 이하로 입력해 주세요.");
        }
        Member member = repository.findById(memberId)
            .filter(m -> "ACTIVE".equals(m.getStatus()))
            .filter(this::isRecoverable)
            .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        member.setPassword(passwordEncoder.encode(newPassword));
        repository.save(member);
    }

    // ---- helpers ----

    private boolean isRecoverable(Member m) {
        return m.getUsername() != null && !"ADMIN".equals(m.getRole());
    }

    private static String clean(String s) { return s == null ? "" : s.trim(); }

    private static String digits(String s) { return s == null ? "" : s.replaceAll("\\D", ""); }

    private void assertNotLocked(String key) {
        Deque<Long> q = fails.get(key);
        if (q == null) return;
        long now = System.currentTimeMillis();
        synchronized (q) {
            while (!q.isEmpty() && now - q.peekFirst() > WINDOW_MS) q.pollFirst();
            if (q.size() >= MAX_FAILS) {
                throw new TooManyAttemptsException("시도 횟수가 너무 많습니다. 10분 후 다시 시도해 주세요.");
            }
        }
    }

    private void recordFail(String key) {
        if (fails.size() > 10000) {
            long now = System.currentTimeMillis();
            fails.values().removeIf(d -> { synchronized (d) { return d.isEmpty() || now - d.peekLast() > WINDOW_MS; } });
        }
        Deque<Long> q = fails.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (q) { q.addLast(System.currentTimeMillis()); }
    }
}