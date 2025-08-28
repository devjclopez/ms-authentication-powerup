package co.com.pragma.api;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

  @RouterOperations({
      @RouterOperation(
          path = "/api/v1/usuarios",
          produces = {MediaType.APPLICATION_JSON_VALUE},
          method = RequestMethod.POST,
          beanClass = Handler.class,
          beanMethod = "listenRegisterUser"
      )
  })
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/usuarios"), handler::listenRegisterUser);
    }
}
