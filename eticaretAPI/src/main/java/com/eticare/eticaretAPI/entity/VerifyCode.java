package com.eticare.eticaretAPI.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Table(name="VerifyCode")
public class VerifyCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(unique = true,nullable = false)
    private  String code;

    private  String verifyToken;

    private LocalDateTime codeExpiryDate;


    private Integer remainingAttempts;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastSendDate = new Date();

    @OneToOne
    private  User user ;


}
