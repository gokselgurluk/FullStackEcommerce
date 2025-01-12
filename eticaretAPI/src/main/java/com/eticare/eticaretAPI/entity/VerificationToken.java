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

@Table(name="VerificationTokens")
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(unique = true,nullable = false)
    private  String code;

    private LocalDateTime codeExpiryDate;

    private Integer sendCount = 0;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastSendDate = new Date();

    @OneToOne
    private  User user ;


}
