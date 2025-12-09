package gr.aueb.cf.webstore.service;

import gr.aueb.cf.webstore.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_ADDRESS);
        message.setTo(to);
        message.setSubject("Verify your email address");
        message.setText("""

                Please verify your email address by clicking the link below:
                %s


                This verification link/token expires in 30 minutes.

                If you did not register for an account, you can safely ignore this email.
                """.formatted(verificationLink, token)
        );

        mailSender.send(message);
        log.info("Email verification email sent to {}", to);
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

}
