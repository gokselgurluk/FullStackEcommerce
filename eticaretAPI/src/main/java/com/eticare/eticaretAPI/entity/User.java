package com.eticare.eticaretAPI.entity;

import com.eticare.eticaretAPI.entity.enums.RoleEnum;
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
    private Long id;

    @NotBlank(message = "Name cannot be empty or blank")
    private String username;

    @NotBlank(message = "Surname cannot be empty or blank")
    private String surname;

    @NotBlank(message = "Password cannot be empty or blank")
    private String password; // Şifre saklanmadan önce hashlenmeli

    @Email(message = "Email must be a valid format")
    @NotBlank(message = "Email cannot be empty or blank")
    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private RoleEnum roleEnum = RoleEnum.USER;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date(); // Varsayılan olarak kayıt zamanı

    @Temporal(TemporalType.TIMESTAMP) // Son Giriş Tarihi
    private Date lastLogin;

    private boolean active = false; // Varsayılan olarak kullanıcı aktif değil

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orderList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CMS> cmsContent = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Token> tokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions = new ArrayList<>();

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", surname='" + surname + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", roleEnum=" + roleEnum +
                ", createdAt=" + createdAt +
                ", lastLogin=" + lastLogin +
                ", active=" + active +
                ", orderList=" + (orderList != null ? orderList.size() + " orders" : "null") +
                ", reviews=" + (reviews != null ? reviews.size() + " reviews" : "null") +
                ", cmsContent=" + (cmsContent != null ? cmsContent.size() + " contents" : "null") +
                ", tokens=" + (tokens != null ? tokens.size() + " tokens" : "null") +
                ", payments=" + (payments != null ? payments.size() + " payments" : "null") +
                ", sessions=" + (sessions != null ? sessions.size() + " sessions" : "null") +
                '}';
    }
}
