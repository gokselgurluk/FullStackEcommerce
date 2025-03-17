package com.eticare.eticaretAPI.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(nullable = false)
    private String email;

    /*@Column(nullable = false)
    private String refreshToken;*/

    @Column(nullable = false)
    private String ipAddress;

    @Builder.Default
    private int incrementFailedAttempts = 0;

    private String browser; // Tarayıcı bilgisi
    private String os;      // İşletim sistemi bilgisi
    private String deviceInfo;  // Cihaz türü bilgisi

    @Builder.Default
    private boolean isVerifiedSession = true;

    @Column(name = "session_created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date(); // Oturumun oluşturulma zamanı

    @Column(name = "session_expires_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt; // Oturumun geçerlilik süresi

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Oturumun sahibi kullanıcı

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "token_id", unique = true, nullable = false)
    private Token token;

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", browser='" + browser + '\'' +
                ", os='" + os + '\'' +
                ", device='" + deviceInfo + '\'' +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", user=" + user +
                ", token=" + token +
                '}';
    }

}