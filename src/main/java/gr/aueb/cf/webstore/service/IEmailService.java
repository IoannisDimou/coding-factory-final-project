package gr.aueb.cf.webstore.service;

public interface IEmailService {

    void sendTwoFactorCode(String to, String code);
}
