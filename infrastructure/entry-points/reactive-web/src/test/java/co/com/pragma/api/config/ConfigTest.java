package co.com.pragma.api.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import co.com.pragma.api.Handler;
import co.com.pragma.api.RouterRest;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
@TestPropertySource(properties = {"cors.allowed-origins=http://localhost:8080"})
class ConfigTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockBean
  private UserUseCase userUseCase;

  private User validUser;

  @BeforeEach
  void setUp() {

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
    
    when(userUseCase.registerUser(any(User.class))).thenReturn(Mono.just(validUser));
  }

  @Test
  void shouldSetSecurityHeaders() {
    webTestClient.post()
        .uri("/api/v1/usuarios")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{}")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().valueEquals("Content-Security-Policy",
            "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
        .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
        .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
        .expectHeader().valueEquals("Server", "")
        .expectHeader().valueEquals("Cache-Control", "no-store")
        .expectHeader().valueEquals("Pragma", "no-cache")
        .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
  }

  @Test
  void corsShouldRejectNotAllowedOrigin() {
    webTestClient.post()
        .uri("/api/v1/usuarios")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{}")
        .header("Origin", "http://not-allowed.com")
        .exchange()
        .expectStatus().isForbidden();
  }
}