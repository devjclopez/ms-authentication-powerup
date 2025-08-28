package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.UserValidator;
import co.com.pragma.model.user.exceptions.InvalidUserException;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

  private final UserRepository userRepository;

  public Mono<User> registerUser(User user) {
    return Mono.justOrEmpty(user)
        .switchIfEmpty(Mono.error(new InvalidUserException("El usuario no puede ser nulo")))
        .map(userToValidate -> {
          UserValidator.validate(userToValidate);
          return userToValidate;
        })
        .flatMap(userRepository::register);
  }
}
