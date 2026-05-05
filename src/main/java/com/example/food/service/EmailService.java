package com.example.food.service;

import com.example.food.exception.AppException;
import com.example.food.exception.ErrorCode;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendScoreEmail(String to, String username, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Mã xác thực quên mật khẩu của bạn");

            String content = """
                    <h2>Thông báo Mã xác thực</h2>
                    <p>Xin chào <b>%s</b>,</p>
                    <p>Mã OTP của bạn là: <span style="color:blue;font-size:20px;"><b>%s</b></span></p>
                    <br/>
                    <p>Chúc bạn ngon miệng 🚀</p>
                    """.formatted(username, otp);

            helper.setText(content, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new AppException(ErrorCode.EMAIL_FAIL);
        }
    }
}
