package com.namnguyen1409.usermanagement.configuration;

import com.namnguyen1409.usermanagement.entity.User;
import com.namnguyen1409.usermanagement.service.UserService;
import com.namnguyen1409.usermanagement.utils.LogUtils;
import com.namnguyen1409.usermanagement.utils.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LogMiddleware extends OncePerRequestFilter {
    String[] ignoredField = {"password", "confirmPassword", "oldPassword", "newPassword", "confirmNewPassword", "accessToken", "refreshToken", "token"};
    LogUtils logUtils;
    UserService userService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString();
        request.setAttribute("requestId", requestId);
        String ipAddress = request.getHeader("X-Forwarded-For") != null
                ? request.getHeader("X-Forwarded-For")
                : request.getRemoteAddr();
        User user;
        try {
            user = userService.getCurrentUserIfExists();
        } catch (Exception e) {
            user = null;
        }
        String username = (user != null) ? user.getUsername() : "anonymous";

        String method = request.getMethod();
        String uri = request.getRequestURI();

        long startTime = System.currentTimeMillis();

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {


            filterChain.doFilter(requestWrapper, responseWrapper);

            var requestBodyObject = logUtils.getBody(requestWrapper.getContentAsByteArray(), request.getCharacterEncoding());

            log.info("""
                            
                            {}
                                ID       : {}
                                User     : {}
                                Method   : {}
                                URI      : {}
                                IP       : {}
                                Request  : {}
                            """, logUtils.tag("REQUEST", LogUtils.BLUE), requestId, username, method, uri, ipAddress
                    , logUtils.logObject(requestBodyObject, ignoredField)
            );

            int status = responseWrapper.getStatus();
            long duration = System.currentTimeMillis() - startTime;

            var responseBodyObject = logUtils.getBody(responseWrapper.getContentAsByteArray(), response.getCharacterEncoding());

            log.info("""
                            
                            {}
                                ID       : {}
                                Status   : {}
                                Duration : {} ms
                                URI      : {}
                                Method   : {}
                                User     : {}
                                Response : {}
                            """,
                    logUtils.tag("RESPONSE", LogUtils.CYAN),
                    requestId,
                    logUtils.colorStatus(status, String.valueOf(status)),
                    duration,
                    uri,
                    method,
                    username,
                    logUtils.logObject(responseBodyObject, ignoredField));
            responseWrapper.copyBodyToResponse();

        } catch (Exception e) {
            responseWrapper.copyBodyToResponse();
            throw e;
        }
    }
}
