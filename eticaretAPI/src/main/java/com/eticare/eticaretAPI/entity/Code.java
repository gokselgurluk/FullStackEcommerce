package com.eticare.eticaretAPI.entity;

import com.eticare.eticaretAPI.entity.enums.TokenType;
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

@Table(name="Code")
public class Code {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(unique = true,nullable = false)
    private  String code;

    private boolean revoked; // code iptal edildi mi?

    private boolean expired; // code süresi doldu mu?

    private LocalDateTime codeExpiryDate;

    private Integer remainingAttempts;

    @Column(name="code_type")
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastSendDate = new Date();

    @OneToOne
    private  User user ;


}
