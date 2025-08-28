package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exceptions.InvalidUserException;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class UserPostgresReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    User,
    UserEntity,
    Long,
    UserPostgresReactiveRepository
    > implements UserRepository {

  private final TransactionalOperator transactionalOperator;

  public UserPostgresReactiveRepositoryAdapter(UserPostgresReactiveRepository repository,
      ObjectMapper mapper, TransactionalOperator transactionalOperator) {
    /**
     *  Could be use mapper.mapBuilder if your domain model implement builder pattern
     *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
     *  Or using mapper.map with the class of the object model
     */
    super(repository, mapper, d -> mapper.map(d, User.class/* change for domain model */));
    this.transactionalOperator = transactionalOperator;
  }

  @Override
  public Mono<User> register(User user) {
    return repository.existsByEmail(user.getEmail())
        .flatMap(exists -> exists
            ? Mono.error(new InvalidUserException("El email ya se encuentra registrado"))
            : super.save(user))
        .as(transactionalOperator::transactional);
  }
}
