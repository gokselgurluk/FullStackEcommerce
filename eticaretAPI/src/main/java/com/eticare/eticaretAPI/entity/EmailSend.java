package com.eticare.eticaretAPI.entity;

import com.eticare.eticaretAPI.entity.enums.SecretTypeEnum;
import com.eticare.eticaretAPI.entity.enums.TokenType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name="email_send")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailSend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="secret_type")
    private SecretTypeEnum secretTypeEnum; // "TOKEN" veya "CODE"

    @Enumerated(EnumType.STRING)
    @Column(name="email_type")
    private TokenType tokenType; // "ACCESS", "RESET_PASSWORD", "ACTIVATION" gibi

    private String value; // Token veya kodun içeriği

    private Integer remainingAttempts = 3; // İlgili işlem için kalan deneme hakkı
    @Temporal(TemporalType.TIMESTAMP)
    private Date emailExpiryDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastSendDate = new Date(); // Son gönderim tarihi

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="token_id")
    @JsonIgnore // Token'ın serileştirilmesini engeller
    private Token token;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="code_id")
    @JsonIgnore // Code'un serileştirilmesini engeller
    private Code code;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore // Kullanıcı bilgisinin serileştirilmesini engeller
    private User user;


    @Override
    public String toString() {
        return "EmailSend{" +
                "id=" + id +
                ", secretTypeEnum=" + secretTypeEnum +
                ", tokenType=" + tokenType +
                ", value='" + value + '\'' +
                ", remainingAttempts=" + remainingAttempts +
                ", emailExpiryDate=" + emailExpiryDate +
                ", lastSendDate=" + lastSendDate +
                ", token=" + token +
                ", code=" + code +
                ", user=" + user +
                '}';
    }
}
