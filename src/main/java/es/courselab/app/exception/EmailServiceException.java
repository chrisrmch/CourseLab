package es.courselab.app.exception;

public class EmailServiceException extends RuntimeException {

    public EmailServiceException(String message) {
        super(message);
    }

    public EmailServiceException(long id_user) {
        super("Email Service Error.");
    }
}
