package com.hderma.clinic.domain.irb;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IrbEmailService {

    private final JavaMailSender mailSender;

    @Value("${email.from:noreply@hderma.co.kr}")
    private String from;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public void sendNotification(List<IrbDto.EmailRecipient> recipients, String subject, String title, Long irbId) {
        if (recipients == null || recipients.isEmpty()) return;
        String url = baseUrl + "/irb/detail?id=" + irbId;
        String html = "<p>" + title + "</p><p><a href=\"" + url + "\">심사 참여하러 가기</a></p>";

        for (IrbDto.EmailRecipient r : recipients) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(from);
                helper.setTo(r.getEmail());
                helper.setSubject(subject);
                helper.setText(html, true);
                mailSender.send(message);
            } catch (Exception e) {
                // 메일 발송 실패는 전체 로직을 막지 않고 로그만 남김
                System.err.println("IRB 메일 발송 실패: " + r.getEmail() + " - " + e.getMessage());
            }
        }
    }
}