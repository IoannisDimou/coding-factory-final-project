package gr.aueb.cf.webstore.core.exceptions;

public class AppObjectAlreadyExists extends AppGenericException {

    private static final String DEFAULT_CODE = "AlreadyExists";

    public AppObjectAlreadyExists(String message, String code) {
        super(code + DEFAULT_CODE, message);
    }
}
