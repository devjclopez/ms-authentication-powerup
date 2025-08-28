package co.com.pragma.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exceptions.InvalidUserException;
import co.com.pragma.usecase.user.UserUseCase;
import java.time.LocalDate;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

  @Mock
  private UserUseCase userUseCase;

  @Mock
  private ServerRequest request;

  private Handler handler;
  private User validUser;

  @BeforeEach
  void setUp() {
    handler = new Handler(userUseCase);
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
  void shouldHandleValidUserRegistration() {
    when(request.bodyToMono(User.class)).thenReturn(Mono.just(validUser));
    when(userUseCase.registerUser(any(User.class))).thenReturn(Mono.just(validUser));

    StepVerifier.create(handler.listenRegisterUser(request))
        .expectNextMatches(response -> {
          assert response.statusCode().is2xxSuccessful();
          assert Objects.equals(response.headers().getContentType(), MediaType.APPLICATION_JSON);
          return true;
        })
        .verifyComplete();

    verify(userUseCase).registerUser(validUser);
  }

  @Test
  void shouldHandleInvalidUserException() {
    when(request.bodyToMono(User.class)).thenReturn(Mono.just(validUser));
    when(userUseCase.registerUser(any(User.class)))
        .thenReturn(Mono.error(new InvalidUserException("El email ya se encuentra registrado")));

    StepVerifier.create(handler.listenRegisterUser(request))
        .expectNextMatches(response -> {
          assert response.statusCode().value() == 400;
          assert Objects.equals(response.headers().getContentType(), MediaType.APPLICATION_JSON);
          return true;
        })
        .verifyComplete();
  }

  @Test
  void shouldHandleGenericException() {
    when(request.bodyToMono(User.class)).thenReturn(Mono.just(validUser));
    when(userUseCase.registerUser(any(User.class)))
        .thenReturn(Mono.error(new RuntimeException("Error interno")));

    StepVerifier.create(handler.listenRegisterUser(request))
        .expectNextMatches(response -> {
          assert response.statusCode().value() == 500;
          assert Objects.equals(response.headers().getContentType(), MediaType.APPLICATION_JSON);
          return true;
        })
        .verifyComplete();
  }

}
