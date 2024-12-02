package com.eticare.eticaretAPI.entity;

import com.eticare.eticaretAPI.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder


@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id ;

    @NotBlank(message = "Name cannot be empty or blank")
    private String name ;

    @NotBlank(message = "Surname cannot be empty or blank")
    private String surname;

    @NotBlank(message ="Password cannot be empty or blank")
    private String password;// Şifre saklanmadan önce hashlenmeli

    @Email(message = "Email must be a valid format")
    @NotBlank(message = "Email cannot be empty or blank")
    @Column(unique = true)
    private String mail;


    @Enumerated(EnumType.STRING)
    private Role roleEnum;


    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date(); // Varsayılan olarak kayıt zamanı

    @Temporal(TemporalType.TIMESTAMP) //Son Giriş Tarihi
    private Date lastLogin;

    private  boolean active =true ;// Varsayılan olarak kullanıcı aktif
}
