package com.namnguyen1409.usermanagement.utils;


import com.namnguyen1409.usermanagement.exception.JwtAuthenticationException;
import com.namnguyen1409.usermanagement.service.cache.TokenBlackListCacheService;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class JwtUtils {

    SecurityUtils securityUtils;
    TokenBlackListCacheService tokenBlackListCacheService;

    @NonFinal
    NimbusJwtDecoder nimbusJwtDecoder;

    @PostConstruct
    public void initDecoder() {
        this.nimbusJwtDecoder = NimbusJwtDecoder.withPublicKey(securityUtils.loadPublicKey()).build();
    }

    public Jwt decode(String token) throws JwtAuthenticationException {
        Jwt jwt;
        try {
            jwt = nimbusJwtDecoder.decode(token);
        } catch (Exception e) {
            throw new JwtAuthenticationException(e.getMessage());
        }
        var jti = jwt.getClaims().get("jti").toString();
        if (Objects.nonNull(jti) && tokenBlackListCacheService.isTokenInBlackList(jti)) {
            throw new JwtAuthenticationException("Token is blacklisted");
        }
        return jwt;
    }
}
