package com.namnguyen1409.usermanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
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
@Table(name = "LOGIN_LOGS")
@FieldNameConstants
public class LoginLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @OneToOne(mappedBy = "loginLog", cascade = CascadeType.ALL)
    RefreshToken refreshToken;

    @Column
    String jti;

    @Column
    Boolean logout = false;

    @CreatedDate
    @Column(nullable = false)
    LocalDateTime createdAt;

    @Column
    LocalDateTime expiredAt;

    @Column(nullable = false)
    Boolean success;

    @Column(nullable = false)
    String userAgent;

    @Column(nullable = false)
    String ipAddress;

    @Column(nullable = false)
    String device;

    @Column(nullable = false)
    String browser;

    @Column(nullable = false)
    String browserVersion;

    @Column(nullable = false)
    String os;

    @Column(nullable = false)
    String osVersion;

}
