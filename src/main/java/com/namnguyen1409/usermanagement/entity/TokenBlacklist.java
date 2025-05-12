package com.namnguyen1409.usermanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
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
@Table(name = "TOKEN_BLACKLIST")
public class TokenBlacklist {
    @Id
    String tokenId;
    @Column(nullable = false)
    LocalDateTime expiredAt;
}
