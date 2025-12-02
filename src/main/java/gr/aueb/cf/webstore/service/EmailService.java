package gr.aueb.cf.webstore.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService implements IEmailService {

    private static final String FROM_ADDRESS = "cf.webstore.noreply@gmail.com";

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendTwoFactorCode(String to, String code) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_ADDRESS);
        message.setTo(to);
        message.setSubject("Your two-factor authentication code");
        message.setText("""
                Your verification code is: %s
                
                This code expires in 5 minutes.
                If you did not request this, you can ignore this email.
                """.formatted(code)

        );
        mailSender.send(message);
        log.info("2FA email sent to {}", to);
    }
}
