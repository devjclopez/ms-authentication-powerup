package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.UserValidator;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

  private final UserRepository userRepository;

  public Mono<User> registerUser(User user) {
    return Mono.just(user)
        .map(userToValidate -> {
          UserValidator.validate(userToValidate);
          return userToValidate;
        })
        .flatMap(userRepository::register);
  }
}
