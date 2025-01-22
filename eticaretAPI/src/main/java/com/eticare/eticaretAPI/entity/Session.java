package com.eticare.eticaretAPI.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "sessions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"user", "token"}) // Döngüsel referansı dışarıda bırakır
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String refreshToken;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private String deviceInfo;

    @Column(name = "session_created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date(); // Oturumun oluşturulma zamanı

    @Column(name = "session_expires_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt; // Oturumun geçerlilik süresi

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Oturumun sahibi kullanıcı

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "token_id", nullable = false)
    private Token token; // Oturumun ilgili Refresh Token'ı
}