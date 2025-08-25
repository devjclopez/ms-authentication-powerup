package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UserPostgresReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    User,
    UserEntity,
    Long,
    UserPostgresReactiveRepository
    > implements UserRepository {

  public UserPostgresReactiveRepositoryAdapter(UserPostgresReactiveRepository repository,
      ObjectMapper mapper) {
    /**
     *  Could be use mapper.mapBuilder if your domain model implement builder pattern
     *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
     *  Or using mapper.map with the class of the object model
     */
    super(repository, mapper, d -> mapper.map(d, User.class/* change for domain model */));
  }

  @Override
  public Mono<User> register(User user) {
    return super.save(user);
  }
  
  @Override
  public Mono<Boolean> existUserByEmail(String email) {
    return repository.existsByEmail(email);
  }
}
