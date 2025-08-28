package co.com.pragma.r2dbc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exceptions.InvalidUserException;
import co.com.pragma.r2dbc.entity.UserEntity;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UserPostgresReactiveRepositoryAdapterTest {

  @Mock
  private UserPostgresReactiveRepository repository;

  @Mock
  private ObjectMapper mapper;

  @Mock
  private TransactionalOperator transactionalOperator;

  private UserPostgresReactiveRepositoryAdapter adapter;
  private User validUser;
  private UserEntity validUserEntity;

  @BeforeEach
  void setUp() {
    adapter = new UserPostgresReactiveRepositoryAdapter(repository, mapper, transactionalOperator);

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

    validUserEntity = UserEntity.builder()
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
  void shouldRegisterUserSuccessfully() {
    when(repository.existsByEmail(validUser.getEmail()))
        .thenReturn(Mono.just(false));
    when(mapper.map(any(User.class), eq(UserEntity.class)))
        .thenReturn(validUserEntity);
    when(repository.save(any(UserEntity.class)))
        .thenReturn(Mono.just(validUserEntity));
    when(mapper.map(any(UserEntity.class), eq(User.class)))
        .thenReturn(validUser);
    when(transactionalOperator.transactional(any(Mono.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    StepVerifier.create(adapter.register(validUser))
        .expectNext(validUser)
        .verifyComplete();

    verify(repository).existsByEmail(validUser.getEmail());
    verify(repository).save(any(UserEntity.class));
    verify(transactionalOperator).transactional(any(Mono.class));
  }

  @Test
  void shouldFailWhenEmailAlreadyExists() {
    when(repository.existsByEmail(validUser.getEmail()))
        .thenReturn(Mono.just(true));
    when(transactionalOperator.transactional(any(Mono.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    StepVerifier.create(adapter.register(validUser))
        .expectErrorMatches(throwable ->
            throwable instanceof InvalidUserException &&
                throwable.getMessage().equals("El email ya se encuentra registrado"))
        .verify();

    verify(repository).existsByEmail(validUser.getEmail());
    verify(repository, never()).save(any(UserEntity.class));
    verify(transactionalOperator).transactional(any(Mono.class));
  }

  @Test
  void shouldHandleRepositoryError() {
    when(repository.existsByEmail(validUser.getEmail()))
        .thenReturn(Mono.error(new RuntimeException("Database error")));
    when(transactionalOperator.transactional(any(Mono.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    StepVerifier.create(adapter.register(validUser))
        .expectError(RuntimeException.class)
        .verify();

    verify(repository).existsByEmail(validUser.getEmail());
    verify(repository, never()).save(any(UserEntity.class));
    verify(transactionalOperator).transactional(any(Mono.class));
  }

  @Test
  void shouldUseTransactionalOperator() {
    when(repository.existsByEmail(validUser.getEmail()))
        .thenReturn(Mono.just(false));
    when(mapper.map(any(User.class), eq(UserEntity.class)))
        .thenReturn(validUserEntity);
    when(repository.save(any(UserEntity.class)))
        .thenReturn(Mono.just(validUserEntity));
    when(mapper.map(any(UserEntity.class), eq(User.class)))
        .thenReturn(validUser);

    // Simular comportamiento transaccional
    when(transactionalOperator.transactional(any(Mono.class)))
        .thenAnswer(invocation -> {
          Mono<?> mono = invocation.getArgument(0);
          return mono; // Simula la envoltura transaccional
        });

    adapter.register(validUser).block();

    verify(transactionalOperator).transactional(any(Mono.class));
  }

  @Test
  void shouldRollbackOnError() {
    when(repository.existsByEmail(validUser.getEmail()))
        .thenReturn(Mono.just(false));
    when(mapper.map(any(User.class), eq(UserEntity.class)))
        .thenReturn(validUserEntity);
    when(repository.save(any(UserEntity.class)))
        .thenReturn(Mono.error(new RuntimeException("Save error")));
    when(transactionalOperator.transactional(any(Mono.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    StepVerifier.create(adapter.register(validUser))
        .expectError(RuntimeException.class)
        .verify();

    verify(repository).existsByEmail(validUser.getEmail());
    verify(repository).save(any(UserEntity.class));
    verify(transactionalOperator).transactional(any(Mono.class));
  }
}
