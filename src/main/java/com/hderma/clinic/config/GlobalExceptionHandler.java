package com.hderma.clinic.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 서비스에서 던지는 검증 오류 메시지를 화면에 그대로 전달하기 위한 공통 처리기.
 * (없으면 IllegalArgumentException이 500 에러로 응답되어 화면에는 이유가 안 보임)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("요청 검증 실패: {}", e.getMessage());   // 스택트레이스 대신 한 줄만 남김
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(DataIntegrityViolationException e) {
        log.warn("데이터 중복/제약 오류: {}", e.getMostSpecificCause().getMessage());
        return ResponseEntity.badRequest()
            .body(Map.of("message", "이미 등록된 정보(아이디 또는 이메일)입니다. 입력하신 내용을 확인해 주세요."));
    }
}