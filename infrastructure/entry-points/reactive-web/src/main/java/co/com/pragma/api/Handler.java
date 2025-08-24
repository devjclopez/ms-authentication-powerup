package co.com.pragma.api;

import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

  private  final UserUseCase userUseCase;

  public Mono<ServerResponse> listenRegisterUser(ServerRequest serverRequest) {
    return serverRequest.bodyToMono(User.class)
        .flatMap(userUseCase::register)
        .flatMap(savedTask -> ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(savedTask));
  }
}
