package com.namnguyen1409.usermanagement.configuration;

import com.fasterxml.jackson.core.ObjectCodec;
import com.namnguyen1409.usermanagement.dto.request.IntrospectRequest;
import com.namnguyen1409.usermanagement.service.AuthenticationService;
import com.namnguyen1409.usermanagement.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {

    AuthenticationService authenticationService;
    SecurityUtils securityUtils;
    private final ObjectCodec objectCodec;

    @NonFinal
    private NimbusJwtDecoder nimbusJwtDecoder;

    @Override
    public Jwt decode(String token) throws JwtException {

        if (Objects.isNull(nimbusJwtDecoder)) {
            nimbusJwtDecoder = NimbusJwtDecoder.withPublicKey(securityUtils.loadPublicKey()).build();
        }
        Jwt jwt = nimbusJwtDecoder.decode(token);
        if (jwt.getClaims().containsKey("jti")) {
            IntrospectRequest introspectRequest = IntrospectRequest.builder()
                    .jti(jwt.getClaims().get("jti").toString())
                    .build();
            authenticationService.introspect(introspectRequest);
            return jwt;
        }
        throw new JwtException("Invalid token");
    }
}
