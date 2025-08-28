package co.com.pragma.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.UserUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class UseCasesConfigTest {

  //    @Test
//    void testUseCaseBeansExist() {
//        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
//            String[] beanNames = context.getBeanDefinitionNames();
//
//            boolean useCaseBeanFound = false;
//            for (String beanName : beanNames) {
//                if (beanName.endsWith("UseCase")) {
//                    useCaseBeanFound = true;
//                    break;
//                }
//            }
//
//            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
//        }
//    }
//
//    @Configuration
//    @Import(UseCasesConfig.class)
//    static class TestConfig {
//
//        @Bean
//        public MyUseCase myUseCase() {
//            return new MyUseCase();
//        }
//    }
//
//    static class MyUseCase {
//        public String execute() {
//            return "MyUseCase Test";
//        }
//    }

  @Mock
  private UserRepository usuarioRepository;

  @Test
  void guardarUsuarioUseCaseBeanIsCreatedTest() {

    UseCasesConfig config = new UseCasesConfig();
    UserUseCase useCase = config.registerUser(usuarioRepository);
    assertNotNull(useCase);
  }
}