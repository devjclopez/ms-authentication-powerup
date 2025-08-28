package co.com.pragma.model.user;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
  
  private String name;
  private String lastName;
  private LocalDate birthDate;
  private String address;
  private String phone;
  private String email;
  private Double baseSalary;
  private String idDocument;
  private String rol;
}
