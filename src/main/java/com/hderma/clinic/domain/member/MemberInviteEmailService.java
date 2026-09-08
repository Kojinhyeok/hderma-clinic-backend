package com.hderma.clinic.domain.member;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberInviteEmailService {

    private final JavaMailSender mailSender;

    @Value("${email.from:noreply@hderma.co.kr}")
    private String from;

    public void sendInvite(String toEmail, String name, String signupUrl) {
        String html = "<p>" + name + "님, H-Derma Global Clinical Center 회원가입 초대입니다.</p>"
                + "<p><a href=\"" + signupUrl + "\">가입 완료하기</a></p>"
                + "<p>이 링크는 7일간 유효합니다.</p>";
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject("[H-Derma] 회원가입 초대");
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("초대 메일 발송 실패: " + toEmail + " - " + e.getMessage());
        }
    }
}