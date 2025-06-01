package es.courselab.app.exception;

public class AccountNotFoundThrowable extends RuntimeException {
  public AccountNotFoundThrowable(String message) {
    super(message);
  }
}
