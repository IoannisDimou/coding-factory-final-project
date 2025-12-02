package gr.aueb.cf.webstore.service;

import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.dto.TwoFactorChallengeDTO;
import gr.aueb.cf.webstore.dto.TwoFactorVerificationRequestDTO;

public interface ITwoFactorService {

    TwoFactorChallengeDTO createTwoFactorChallenge(String email, String deliveryMethod) throws AppObjectNotFoundException,
            AppObjectInvalidArgumentException;

    String verifyTwoFactorCode(TwoFactorVerificationRequestDTO request) throws AppObjectInvalidArgumentException;
}
