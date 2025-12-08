package gr.aueb.cf.webstore.service;

public interface IEmailService {

    void sendTwoFactorCode(String to, String code);

    void sendEmailVerification(String to, String verificationLink, String token);
}
