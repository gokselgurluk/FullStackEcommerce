package com.eticare.eticaretAPI.entity;

import com.eticare.eticaretAPI.dto.response.UserResponse;
import com.eticare.eticaretAPI.entity.enums.Role;
import com.eticare.eticaretAPI.entity.enums.TokenType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder


@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @NotBlank(message = "Name cannot be empty or blank")
    private String username;

    @NotBlank(message = "Surname cannot be empty or blank")
    private String surname;

    @NotBlank(message ="Password cannot be empty or blank")
    private String password;// Şifre saklanmadan önce hashlenmeli

    @Email(message = "Email must be a valid format")
    @NotBlank(message = "Email cannot be empty or blank")
    @Column(unique = true)
    private String email;


    @Enumerated(EnumType.STRING)
    private Role roleEnum;


    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date(); // Varsayılan olarak kayıt zamanı

    @Temporal(TemporalType.TIMESTAMP) //Son Giriş Tarihi
    private Date lastLogin;

    private  boolean active =true ;// Varsayılan olarak kullanıcı aktif

    @OneToMany(mappedBy = "user" ,cascade=CascadeType.ALL,orphanRemoval = true)
    private List<Order> orderList;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CMS> cmsContent;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Token> tokens ;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();


}
