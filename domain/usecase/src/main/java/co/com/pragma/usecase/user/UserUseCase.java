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
    return Mono.just(user)
        .map(userToValidate -> {
          UserValidator.validate(userToValidate);
          return userToValidate;
        })
        .flatMap(validUser -> validateUniqueEmail(validUser.getEmail())
            .then(Mono.just(validUser)))
        .flatMap(userRepository::register);
  }

  private Mono<Void> validateUniqueEmail(String email) {
    return userRepository.existUserByEmail(email)
        .flatMap(exists -> exists
            ? Mono.error(new InvalidUserException("El email ya se encuentra registrado"))
            : Mono.empty());
  }
}
