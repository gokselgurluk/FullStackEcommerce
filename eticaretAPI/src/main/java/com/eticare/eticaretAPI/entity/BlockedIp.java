package com.eticare.eticaretAPI.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="blocked_ips")
public class BlockedIp {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ipAddresses;


    @Column(name = "blocked_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date blocked_at ;

    @Column(name = "unblocked_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date unblocked_at;

    private  boolean blockedIpStatus;

    private long diffLockedTime ;

    private int incrementFailedAttempts;

    @OneToMany(mappedBy ="blockedIP",cascade = CascadeType.ALL ,fetch = FetchType.LAZY)
    private Set<FailedAttempt> failedAttempts =new HashSet<>();

    @Override
    public String toString() {
        return "BlockedIp{" +
                "id=" + id +
                ", ipAddresses='" + ipAddresses + '\'' +
                ", blockedAt=" + blocked_at +
                ", unblockedAt=" + unblocked_at +
                ", isIpBlocked=" + blockedIpStatus +
                ", diffLockedTime=" + diffLockedTime +
                ", incrementFailedAttempts=" + incrementFailedAttempts +
                ", failedAttempts=" + failedAttempts +
                '}';
    }
}
