package co.com.pragma.model.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.com.pragma.model.user.exceptions.InvalidUserException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class UserValidatorTest {

  private User.UserBuilder validUserBuilder() {
    return User.builder()
        .name("Juan")
        .lastName("Pérez")
        .birthDate(LocalDate.of(1990, 1, 1))
        .address("Calle 123")
        .phone("1234567890")
        .email("juan.perez@email.com")
        .baseSalary(1000000.0)
        .idDocument("12345678")
        .rol("USER");
  }

  @Test
  void shouldValidateValidUser() {
    User user = validUserBuilder().build();
    assertDoesNotThrow(() -> UserValidator.validate(user));
  }

  @Test
  void shouldThrowWhenUserIsNull() {
    InvalidUserException exception = assertThrows(
        InvalidUserException.class,
        () -> UserValidator.validate(null)
    );
    assertEquals("El usuario no puede ser nulo", exception.getMessage());
  }

  @Test
  void shouldThrowWhenNameIsInvalid() {
    // Test null name
    User userWithNullName = validUserBuilder().name(null).build();
    InvalidUserException exNull = assertThrows(
        InvalidUserException.class,
        () -> UserValidator.validate(userWithNullName)
    );
    assertEquals("El nombre es requerido", exNull.getMessage());

    // Test empty name
    User userWithEmptyName = validUserBuilder().name("").build();
    InvalidUserException exEmpty = assertThrows(
        InvalidUserException.class,
        () -> UserValidator.validate(userWithEmptyName)
    );
    assertEquals("El nombre es requerido", exEmpty.getMessage());

    // Test blank name
    User userWithBlankName = validUserBuilder().name("   ").build();
    InvalidUserException exBlank = assertThrows(
        InvalidUserException.class,
        () -> UserValidator.validate(userWithBlankName)
    );
    assertEquals("El nombre es requerido", exBlank.getMessage());
  }

  @Test
  void shouldThrowWhenLastNameIsInvalid() {
    // Test null lastName
    User userWithNullLastName = validUserBuilder().lastName(null).build();
    InvalidUserException exNull = assertThrows(
        InvalidUserException.class,
        () -> UserValidator.validate(userWithNullLastName)
    );
    assertEquals("El apellido es requerido", exNull.getMessage());

    // Test empty lastName
    User userWithEmptyLastName = validUserBuilder().lastName("").build();
    InvalidUserException exEmpty = assertThrows(
        InvalidUserException.class,
        () -> UserValidator.validate(userWithEmptyLastName)
    );
    assertEquals("El apellido es requerido", exEmpty.getMessage());

    // Test blank lastName
    User userWithBlankLastName = validUserBuilder().lastName("   ").build();
    InvalidUserException exBlank = assertThrows(
        InvalidUserException.class,
        () -> UserValidator.validate(userWithBlankLastName)
    );
    assertEquals("El apellido es requerido", exBlank.getMessage());
  }

  @Test
  void shouldThrowWhenEmailIsInvalid() {
    // Test null email
    User userWithNullEmail = validUserBuilder().email(null).build();
    InvalidUserException exNull = assertThrows(
        InvalidUserException.class,
        () -> UserValidator.validate(userWithNullEmail)
    );
    assertEquals("El email es requerido", exNull.getMessage());

    // Test empty email
    User userWithEmptyEmail = validUserBuilder().email("").build();
    InvalidUserException exEmpty = assertThrows(
        InvalidUserException.class,
        () -> UserValidator.validate(userWithEmptyEmail)
    );
    assertEquals("El email es requerido", exEmpty.getMessage());

    // Test invalid email format
    User userWithInvalidEmail = validUserBuilder().email("invalid-email").build();
    InvalidUserException exInvalid = assertThrows(
        InvalidUserException.class,
        () -> UserValidator.validate(userWithInvalidEmail)
    );
    assertEquals("El formato del email no es válido", exInvalid.getMessage());
  }

  @Test
  void shouldThrowWhenBaseSalaryIsInvalid() {
    // Test null salary
    User userWithNullSalary = validUserBuilder().baseSalary(null).build();
    InvalidUserException exNull = assertThrows(
        InvalidUserException.class,
        () -> UserValidator.validate(userWithNullSalary)
    );
    assertEquals("El salario base es requerido", exNull.getMessage());

    // Test negative salary
    User userWithNegativeSalary = validUserBuilder().baseSalary(-1.0).build();
    InvalidUserException exNegative = assertThrows(
        InvalidUserException.class,
        () -> UserValidator.validate(userWithNegativeSalary)
    );
    assertEquals("El salario debe estar entre 0.00 y 15000000.00", exNegative.getMessage());

    // Test excessive salary
    User userWithExcessiveSalary = validUserBuilder().baseSalary(15000001.0).build();
    InvalidUserException exExcessive = assertThrows(
        InvalidUserException.class,
        () -> UserValidator.validate(userWithExcessiveSalary)
    );
    assertEquals("El salario debe estar entre 0.00 y 15000000.00", exExcessive.getMessage());
  }

  @Test
  void shouldValidateValidEmailFormats() {
    User userWithSimpleEmail = validUserBuilder().email("user@domain.com").build();
    assertDoesNotThrow(() -> UserValidator.validate(userWithSimpleEmail));

    User userWithComplexEmail = validUserBuilder().email("user.name+tag@domain.co.uk").build();
    assertDoesNotThrow(() -> UserValidator.validate(userWithComplexEmail));
  }
}
