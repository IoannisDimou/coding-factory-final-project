package gr.aueb.cf.webstore.service;

import gr.aueb.cf.webstore.model.Order;

public interface IEmailService {

    void sendTwoFactorCode(String to, String code);

    void sendEmailVerification(String to, String verificationLink, String token);

    void sendOrderConfirmation(Order order);
}
