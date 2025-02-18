package com.eticare.eticaretAPI.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="failed_attempts")
public class FailedAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Date attemptTime = new Date();

    private String browser; // Tarayıcı bilgisi
    private String os;      // İşletim sistemi bilgisi
    private String deviceInfo;  // Cihaz türü bilgisi


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "blocked_ip_id", nullable = false)
    private BlockedIp blockedIP;
}
