package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

  private final UserRepository userRepository;

  public Mono<User> register(User user) {
    return userRepository.existUserByEmail(user.getEmail())
        .flatMap(exists -> exists
            ? Mono.error(new RuntimeException("El usuario con el correo electrónico ya existe"))
            : userRepository.register(user)
        );
  }

}
