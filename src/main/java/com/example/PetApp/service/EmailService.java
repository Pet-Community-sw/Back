package com.example.PetApp.service;

import com.example.PetApp.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;
    public ResponseEntity sendMail(String toEmail) {
        if (redisUtil.existData(toEmail)) {
            redisUtil.deleteData(toEmail);
        }
        try{
            String emailCode = createCode();
            MimeMessage message = createEmail(toEmail, emailCode);
            javaMailSender.send(message);

            return ResponseEntity.ok().build();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private MimeMessage createEmail(String toEmail, String emailCode) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setRecipients(Message.RecipientType.TO, toEmail);
        message.setSubject("멍냥로드 인증코드 안내입니다.");

        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        sb.append("인증코드를 확인해주세요.<br><strong style=\"font-size: 30px;\">");
        sb.append(emailCode);
        sb.append("</strong><br>인증코드는 3분간 유지됩니다.</div>");

        message.setText(sb.toString(), "utf-8", "html");
        message.setFrom(new InternetAddress("chltjswo789@gmail.com", "멍냥로드"));

        redisUtil.createData(toEmail, emailCode, 3*60L);

        return message;
    }

    private String createCode() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            int n = random.nextInt(10);
            sb.append(n);
        }
        return sb.toString();
    }

    public ResponseEntity verifyCode(String email, String code) {
        String authCode = redisUtil.getData(email);
        if (authCode == null) {
            return ResponseEntity.badRequest().body("인증번호가 만료되었습니다. 다시 시도해주세요.");
        } else if (code.equals(authCode)) {
            return ResponseEntity.ok("인증 성공했습니다.");
        } else {
            return ResponseEntity.badRequest().body("인증번호가 일치하지 않습니다.");
        }
    }
}
