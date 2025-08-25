package co.com.pragma.api;

import co.com.pragma.api.model.ErrorResponse;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exceptions.InvalidUserException;
import co.com.pragma.usecase.user.UserUseCase;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

  private static final Logger log = LoggerFactory.getLogger(Handler.class);
  private final UserUseCase userUseCase;

  public Mono<ServerResponse> listenRegisterUser(ServerRequest request) {
    log.info("Inicio del proceso de registro de usuario");
    return request.bodyToMono(User.class)
        .doOnNext(user -> log.debug("Datos recibidos: {}", user))
        .flatMap(userUseCase::registerUser)
        .doOnSuccess(user -> log.info("Usuario registrado exitosamente: {}", user.getEmail()))
        .flatMap(user -> ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(user))
        .doOnError(e -> log.error("Error durante el registro de usuario: {}", e.getMessage(), e))
        .onErrorResume(InvalidUserException.class, this::handleInvalidUserException)
        .onErrorResume(Exception.class, this::handleGenericException);
  }

  private Mono<ServerResponse> handleInvalidUserException(InvalidUserException ex) {
    return buildErrorResponse(
        ex.getMessage(),
        "Validation Error",
        HttpStatus.BAD_REQUEST
    );
  }

  private Mono<ServerResponse> handleGenericException(Exception ex) {
    return buildErrorResponse(
        "Error interno del servidor",
        ex.getMessage(),
        HttpStatus.INTERNAL_SERVER_ERROR
    );
  }

  private Mono<ServerResponse> buildErrorResponse(String message, String error, HttpStatus status) {
    return ServerResponse
        .status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(ErrorResponse.builder()
            .message(message)
            .error(error)
            .status(status.value())
            .path("/api/v1/usuarios")
            .timestamp(LocalDateTime.now())
            .build()
        );
  }
}
