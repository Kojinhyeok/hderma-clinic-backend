package com.hderma.clinic.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * 관리자 화면(/admin/*.html)과 관리자 전용 API(등록/수정/삭제)를
 * 세션의 role=ADMIN 여부로 보호하는 필터.
 *
 * Spring Security(SecurityConfig)는 지금 개발 편의상 전부 permitAll인데,
 * 이 필터가 그 앞단에서 실질적인 관리자 보호를 담당함.
 */
@Component
public class AdminAuthFilter extends OncePerRequestFilter {

    // 로그인 없이 접근 가능한 admin 하위 경로 (로그인 페이지 자체 + 공용 리소스)
    private static final List<String> OPEN_ADMIN_PREFIXES = List.of(
        "/admin/login.html",
        "/admin/css/",
        "/admin/js/",
        "/admin/img/",
        "/admin/components/"
    );

    // 관리자만 호출 가능한 API prefix (조회(GET)는 열어두고, 등록/수정/삭제만 막는 경우가 많음)
    private static final List<String> ADMIN_API_PREFIXES = List.of(
        "/api/eval-functional", "/api/eval-health-food", "/api/eval-usability",
        "/api/eval-safety", "/api/eval-invitro", "/api/eval-efficacy",
        "/api/certification-marks", "/api/certification-mark-categories",
        "/api/recruitments", "/api/notices", "/api/newsletters", "/api/popups",
        "/api/members", "/api/files/local"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        boolean isAdminPage = path.startsWith("/admin/") && path.endsWith(".html")
            && OPEN_ADMIN_PREFIXES.stream().noneMatch(path::startsWith);

        boolean isAdminApiWrite = ADMIN_API_PREFIXES.stream().anyMatch(path::startsWith)
            && !"GET".equalsIgnoreCase(method);

        // 시험의뢰/시험참여신청은 GET(조회)도 관리자 전용 — 개인정보가 들어있어서
        boolean isSensitiveRead = ("GET".equalsIgnoreCase(method))
            && (path.startsWith("/api/trial-requests") || path.startsWith("/api/trial-applications"));

        if (isAdminPage || isAdminApiWrite || isSensitiveRead) {
            HttpSession session = request.getSession(false);
            Object role = session != null ? session.getAttribute("role") : null;

            if (!"ADMIN".equals(role)) {
                if (isAdminPage) {
                    response.sendRedirect("/admin/login.html");
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"message\":\"관리자 로그인이 필요합니다.\"}");
                }
                return;
            }
        }

        chain.doFilter(request, response);
    }
}