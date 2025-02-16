package com.eticare.eticaretAPI.entity;

import com.eticare.eticaretAPI.entity.enums.TokenType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="Token")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY )
    private Long id;

    @Column(unique = true ,nullable = false)
    @NotBlank(message = "the Token cannot be empty or null")
    private String tokenValue;

    @Column(name = "token_created_at_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created_at =new Date();

    @Column(name = "token_expires_at_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expires_at ;

    @Column(name="token_type")
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean revoked = true; // Token iptal edildi mi?

    private boolean expired; // Token süresi doldu mu?

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private  User user ;

    @OneToOne(mappedBy = "token")
    private EmailSend emailSend;

    @OneToMany(mappedBy = "token", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions = new ArrayList<>();
    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", token='" + tokenValue + '\'' +
                ", created_at=" + created_at +
                ", expires_at=" + expires_at +
                ", tokenType=" + tokenType +
                ", revoked=" + revoked +
                ", expired=" + expired +
                '}';
    }
}
