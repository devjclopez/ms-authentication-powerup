package co.com.pragma.api;

import co.com.pragma.api.model.ErrorResponse;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exceptions.InvalidUserException;
import co.com.pragma.usecase.user.UserUseCase;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

  private final UserUseCase userUseCase;

  public Mono<ServerResponse> listenRegisterUser(ServerRequest request) {
    return request.bodyToMono(User.class)
        .flatMap(userUseCase::registerUser)
        .flatMap(user -> ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(user))
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
