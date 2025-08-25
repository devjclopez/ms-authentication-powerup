package co.com.pragma.api;

import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "User management API")
public class Handler {

  private  final UserUseCase userUseCase;

  @Operation(
      summary = "Registrar usuario",
      description = "Registra un nuevo usuario en el sistema",
      requestBody = @RequestBody(
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = User.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Usuario registrado exitosamente",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = User.class)
              )
          )
      }
  )

  public Mono<ServerResponse> listenRegisterUser(ServerRequest serverRequest) {
    return serverRequest.bodyToMono(User.class)
        .flatMap(userUseCase::register)
        .flatMap(savedTask -> ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(savedTask));
  }
}
