package gr.aueb.cf.webstore.core.exceptions;

public class EmailNotVerifiedException extends AppGenericException {

    private static final String DEFAULT_CODE = "EmailNotVerified";

    public EmailNotVerifiedException(String message) {
        super(DEFAULT_CODE, message);
    }
}

