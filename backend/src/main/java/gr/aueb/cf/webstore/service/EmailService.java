package gr.aueb.cf.webstore.service;
import gr.aueb.cf.webstore.model.Order;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.text.NumberFormat;
import java.util.Locale;

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

    @Override
    public void sendEmailVerification(String to, String verificationLink, String token) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setFrom(FROM_ADDRESS);
            helper.setTo(to);
            helper.setSubject("Verify your email");

            String html = """
            <div>
            <p>Hi,</p>
            <p>Thanks for creating an account at <strong>Arctic Builds</strong>.</p>
            <p>Click the button below to verify your email address:</p>
            <a href="%s"
               style="display:inline-block;padding:10px 16px;
                      background:#111827;color:#ffffff;text-decoration:none;
                      border-radius:6px;font-weight:600;">
              Verify email
            </a>
            <p>This verification link expires in 30 minutes. If you did not register for an account,
               you can safely ignore this email. </p>
            </div>
            """.formatted(verificationLink);

            helper.setText(html, true);
            mailSender.send(mimeMessage);
            log.info("Email verification email sent to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send verification email to {}", to, e);
        }
    }

    @Override
    public void sendOrderConfirmation(Order order) {

        if (order == null || order.getUser() == null) {
            log.warn("Skipping order confirmation email: order or user is null");
            return;
        }

        NumberFormat euroFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        String formattedTotal = euroFormat.format(order.getTotalPrice());

        String to = order.getUser().getEmail();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_ADDRESS);
        message.setTo(to);
        message.setSubject("Order confirmation - " + order.getOrderCode());

        StringBuilder body = new StringBuilder();

        body.append("Thank you for your order!\n\n")
                .append("Order code: ").append(order.getOrderCode()).append("\n")
                .append("Total amount: ").append(formattedTotal).append("\n")
                .append("Status: ").append(order.getStatus()).append("\n\n");

        if (order.getShippingAddress() != null) {
            body.append("Shipping address:\n")
                    .append(order.getShippingAddress().getStreet()).append("\n")
                    .append(order.getShippingAddress().getCity()).append(" ")
                    .append(order.getShippingAddress().getZipcode()).append("\n")
                    .append(order.getShippingAddress().getCountry()).append("\n\n\n");
        }

        body.append("You can view your order details using this code in your account.\n");

        message.setText(body.toString());
        mailSender.send(message);

        log.info("Order confirmation email sent for order id={}, code={}, to={}", order.getId(), order.getOrderCode(), to);
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_ADDRESS);
        message.setTo(to);
        message.setSubject("Reset your password");
        message.setText("""
            You requested a password reset.

            Click the link below to set a new password:
            %s

            This link expires in 10 minutes.

            If you did not request this, you can safely ignore this email.
            """.formatted(resetLink)
        );

        mailSender.send(message);
        log.info("Password reset email sent to={}", to);
    }
}
