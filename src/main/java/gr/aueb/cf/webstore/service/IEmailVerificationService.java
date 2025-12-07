package gr.aueb.cf.webstore.service;

import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.model.User;

public interface IEmailVerificationService {

    void createAndSendToken(User user);

    void verify(String token) throws AppObjectInvalidArgumentException, AppObjectNotFoundException;
}
