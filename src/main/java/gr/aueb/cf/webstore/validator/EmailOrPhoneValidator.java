package gr.aueb.cf.webstore.validator;

import gr.aueb.cf.webstore.dto.UserInsertDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailOrPhoneValidator implements ConstraintValidator<EmailOrPhone, UserInsertDTO> {

    @Override
    public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {
        boolean hasEmail = dto.email() != null && !dto.email().isBlank();
        boolean hasPhone = dto.phoneNumber() != null && !dto.phoneNumber().isBlank();

        if (hasEmail || hasPhone) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("Either email or phone number must be provided")
                .addConstraintViolation();

        return false;
    }
}


