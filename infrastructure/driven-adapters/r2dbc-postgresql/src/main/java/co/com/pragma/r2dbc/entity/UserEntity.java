package co.com.pragma.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("users")
public class UserEntity {

  @Id
  @Column("user_id")
  private Long id;
  @Column("name")
  private String name;
  @Column("last_name")
  private String lastName;
  @Column("birth_date")
  private String birthDate;
  @Column("address")
  private String address;
  @Column("phone")
  private String phone;
  @Column("email")
  private String email;
  @Column("base_salary")
  private Double baseSalary;
  @Column("id_document")
  private String idDocument;
  @Column("rol")
  private String rol;

}
