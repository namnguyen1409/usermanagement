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
@Table(name = "LOGIN_LOGS")
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
    String jti; // JWT ID

    @Column
    Boolean logout = false;

    @CreatedDate
    @Column(nullable = false)
    LocalDateTime createdAt;

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

    @Override
    public String toString() {
        return "LoginLog{" +
                "id='" + id + '\'' +
                ", user=" + user +
                ", createdAt=" + createdAt +
                ", success=" + success +
                ", userAgent='" + userAgent + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", browser='" + browser + '\'' +
                ", browserVersion='" + browserVersion + '\'' +
                ", os='" + os + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", device='" + device + '\'' +
                '}';
    }
}
