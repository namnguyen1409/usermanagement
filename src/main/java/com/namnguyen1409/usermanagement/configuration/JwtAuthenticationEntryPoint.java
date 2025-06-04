package com.namnguyen1409.usermanagement.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namnguyen1409.usermanagement.dto.response.CustomApiResponse;
import com.namnguyen1409.usermanagement.exception.ErrorCode;
import com.namnguyen1409.usermanagement.exception.JwtAuthenticationException;
import com.namnguyen1409.usermanagement.utils.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {


    LogUtils logUtils;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {


        if (authException instanceof JwtAuthenticationException) {
            log.info("JWT Authentication Exception: {}", authException.getMessage());
        }

        String requestId = UUID.randomUUID().toString();
        request.setAttribute("requestId", requestId);

        String ipAddress = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .orElse(request.getRemoteAddr());

        String username = "anonymous";
        String method = request.getMethod();
        String uri = request.getAttribute("start-uri").toString();

        log.info("""
                        
                        {}
                            ID       : {}
                            User     : {}
                            Method   : {}
                            URI      : {}
                            IP       : {}
                            Error    : {}
                        """,
                logUtils.tag("JWT-ERROR", LogUtils.RED),
                requestId,
                username,
                method,
                uri,
                ipAddress,
                authException.getMessage()
        );

        ErrorCode errorCode = ErrorCode.INVALID_TOKEN;

        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        CustomApiResponse<?> customApiResponse = CustomApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(customApiResponse));
        response.flushBuffer();
    }


}
