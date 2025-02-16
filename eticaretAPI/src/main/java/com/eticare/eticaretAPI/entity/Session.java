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

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private String ipAddress;

    private String browser; // Tarayıcı bilgisi
    private String os;      // İşletim sistemi bilgisi
    private String deviceInfo;  // Cihaz türü bilgisi
    private boolean isVerifiedSession ;
    private int incrementFailedAttempts = 0;
    @Column(name = "session_created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date(); // Oturumun oluşturulma zamanı

    @Column(name = "session_expires_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt; // Oturumun geçerlilik süresi

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Oturumun sahibi kullanıcı


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "token_id", nullable = false)
    private Token token; // Oturumun ilgili Refresh Token'ı

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
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