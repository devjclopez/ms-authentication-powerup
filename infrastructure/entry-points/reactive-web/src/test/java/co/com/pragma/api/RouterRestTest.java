package co.com.pragma.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

  @Mock
  private Handler handler;

  @Test
  void shouldRoutePostRequestToHandler() {

    when(handler.listenRegisterUser(any()))
        .thenReturn(Mono.just(
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .block()
        ));

    RouterFunction<ServerResponse> routerFunction = new RouterRest().routerFunction(handler);

    WebTestClient testClient = WebTestClient
        .bindToRouterFunction(routerFunction)
        .build();

    testClient.post()
        .uri("/api/v1/usuarios")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{}")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON);
  }
}
