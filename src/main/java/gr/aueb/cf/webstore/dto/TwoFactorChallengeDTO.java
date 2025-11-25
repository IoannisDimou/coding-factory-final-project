package gr.aueb.cf.webstore.dto;

public record TwoFactorChallengeDTO(
        String twoFactorToken,
        String deliveryMethod,
        String message
) {}

