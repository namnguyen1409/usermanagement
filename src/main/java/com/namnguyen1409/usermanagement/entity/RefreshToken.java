package com.namnguyen1409.usermanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "REFRESH_TOKENS")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, unique = true)
    String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @OneToOne
    @JoinColumn(name = "login_log_id", nullable = false)
    LoginLog loginLog;

    @CreatedDate
    @Column(nullable = false)
    LocalDateTime createdAt;

    @Column(nullable = false)
    LocalDateTime expiresAt;

    @Column(nullable = false)
    Boolean revoked = false;

}
