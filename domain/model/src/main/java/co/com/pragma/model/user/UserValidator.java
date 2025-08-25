package co.com.pragma.model.user;

import co.com.pragma.model.user.exceptions.InvalidUserException;
import java.util.regex.Pattern;

public class UserValidator {

  private static final Pattern EMAIL_PATTERN = Pattern.compile(
      "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
  );
  private static final double MAX_SALARY = 15000000.0;
  private static final double MIN_SALARY = 0.0;

  public static void validate(User user) {
    validateNotNull(user);
    validateRequiredFields(user);
    validateEmail(user.getEmail());
    validateSalary(user.getBaseSalary());
  }

  private static void validateNotNull(User user) {
    if (user == null) {
      throw new InvalidUserException("El usuario no puede ser nulo");
    }
  }

  private static void validateRequiredFields(User user) {
    if (isNullOrBlank(user.getName())) {
      throw new InvalidUserException("El nombre es requerido");
    }
    if (isNullOrBlank(user.getLastName())) {
      throw new InvalidUserException("El apellido es requerido");
    }
    if (isNullOrBlank(user.getEmail())) {
      throw new InvalidUserException("El email es requerido");
    }
    if (user.getBaseSalary() == null) {
      throw new InvalidUserException("El salario base es requerido");
    }
  }

  private static void validateEmail(String email) {
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new InvalidUserException("El formato del email no es válido");
    }
  }

  private static void validateSalary(Double salary) {
    if (salary < MIN_SALARY || salary > MAX_SALARY) {
      throw new InvalidUserException(
          String.format("El salario debe estar entre %.2f y %.2f", MIN_SALARY, MAX_SALARY)
      );
    }
  }

  private static boolean isNullOrBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
