package co.com.pragma.model.user.exceptions;

public class InvalidUserException extends IllegalArgumentException {

  public InvalidUserException(String message) {
    super(message);
  }

}
