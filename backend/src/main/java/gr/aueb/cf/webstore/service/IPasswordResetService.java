package gr.aueb.cf.webstore.service;

import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;

public interface IPasswordResetService {

    void createToken(String email) throws AppObjectNotFoundException;

    void resetPassword(String token, String newPassword) throws AppObjectInvalidArgumentException, AppObjectNotFoundException;
}
