package co.com.pragma.usecase.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exceptions.InvalidUserException;
import co.com.pragma.model.user.gateways.UserRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

  @Mock
  private UserRepository userRepository;

  private UserUseCase userUseCase;
  private User validUser;

  @BeforeEach
  void setUp() {
    userUseCase = new UserUseCase(userRepository);
    validUser = User.builder()
        .name("Juan")
        .lastName("Pérez")
        .birthDate(LocalDate.of(1990, 1, 1))
        .address("Calle 123")
        .phone("1234567890")
        .email("juan.perez@email.com")
        .baseSalary(1000000.0)
        .idDocument("12345678")
        .rol("USER")
        .build();
  }

  @Test
  void shouldRegisterValidUser() {
    when(userRepository.register(any(User.class)))
        .thenReturn(Mono.just(validUser));

    StepVerifier.create(userUseCase.registerUser(validUser))
        .expectNext(validUser)
        .verifyComplete();

    verify(userRepository).register(validUser);
  }

  @Test
  void shouldFailWhenUserIsInvalid() {
    User invalidUser = validUser.toBuilder()
        .email("invalid-email")
        .build();

    StepVerifier.create(userUseCase.registerUser(invalidUser))
        .expectErrorMatches(throwable ->
            throwable instanceof InvalidUserException &&
                throwable.getMessage().equals("El formato del email no es válido"))
        .verify();
  }

  @Test
  void shouldFailWhenRepositoryFails() {
    when(userRepository.register(any(User.class)))
        .thenReturn(Mono.error(new RuntimeException("Database error")));

    StepVerifier.create(userUseCase.registerUser(validUser))
        .expectError(RuntimeException.class)
        .verify();

    verify(userRepository).register(validUser);
  }

  @Test
  void shouldFailWhenUserIsNull() {
    StepVerifier.create(userUseCase.registerUser(null))
        .expectErrorMatches(throwable ->
            throwable instanceof InvalidUserException &&
                throwable.getMessage().equals("El usuario no puede ser nulo"))
        .verify();
  }

  @Test
  void shouldValidateRequiredFields() {
    // Test null name
    User userWithoutName = validUser.toBuilder().name(null).build();
    StepVerifier.create(userUseCase.registerUser(userWithoutName))
        .expectErrorMatches(throwable ->
            throwable instanceof InvalidUserException &&
                throwable.getMessage().equals("El nombre es requerido"))
        .verify();

    // Test null lastName
    User userWithoutLastName = validUser.toBuilder().lastName(null).build();
    StepVerifier.create(userUseCase.registerUser(userWithoutLastName))
        .expectErrorMatches(throwable ->
            throwable instanceof InvalidUserException &&
                throwable.getMessage().equals("El apellido es requerido"))
        .verify();

    // Test null email
    User userWithoutEmail = validUser.toBuilder().email(null).build();
    StepVerifier.create(userUseCase.registerUser(userWithoutEmail))
        .expectErrorMatches(throwable ->
            throwable instanceof InvalidUserException &&
                throwable.getMessage().equals("El email es requerido"))
        .verify();

    // Test null baseSalary
    User userWithoutSalary = validUser.toBuilder().baseSalary(null).build();
    StepVerifier.create(userUseCase.registerUser(userWithoutSalary))
        .expectErrorMatches(throwable ->
            throwable instanceof InvalidUserException &&
                throwable.getMessage().equals("El salario base es requerido"))
        .verify();
  }

  @Test
  void shouldValidateBaseSalaryRange() {
    // Test negative salary
    User userWithNegativeSalary = validUser.toBuilder().baseSalary(-1.0).build();
    StepVerifier.create(userUseCase.registerUser(userWithNegativeSalary))
        .expectErrorMatches(throwable ->
            throwable instanceof InvalidUserException &&
                throwable.getMessage().equals("El salario debe estar entre 0.00 y 15000000.00"))
        .verify();

    // Test excessive salary
    User userWithExcessiveSalary = validUser.toBuilder().baseSalary(15000001.0).build();
    StepVerifier.create(userUseCase.registerUser(userWithExcessiveSalary))
        .expectErrorMatches(throwable ->
            throwable instanceof InvalidUserException &&
                throwable.getMessage().equals("El salario debe estar entre 0.00 y 15000000.00"))
        .verify();
  }

  @Test
  void shouldValidateEmailFormat() {
    User userWithInvalidEmail = validUser.toBuilder().email("not-an-email").build();
    StepVerifier.create(userUseCase.registerUser(userWithInvalidEmail))
        .expectErrorMatches(throwable ->
            throwable instanceof InvalidUserException &&
                throwable.getMessage().equals("El formato del email no es válido"))
        .verify();

    User userWithValidEmail = validUser.toBuilder().email("valid.email@domain.com").build();
    when(userRepository.register(any(User.class)))
        .thenReturn(Mono.just(userWithValidEmail));

    StepVerifier.create(userUseCase.registerUser(userWithValidEmail))
        .expectNext(userWithValidEmail)
        .verifyComplete();
  }
}
