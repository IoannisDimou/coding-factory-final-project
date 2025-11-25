package gr.aueb.cf.webstore.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Constraint(validatedBy = EmailOrPhoneValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailOrPhone {
    String message() default "Email or phone number must be provided";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
