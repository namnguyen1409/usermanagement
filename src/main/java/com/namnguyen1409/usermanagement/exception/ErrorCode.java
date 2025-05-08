package com.namnguyen1409.usermanagement.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public enum ErrorCode {
    UNCATEGORIZED(1000, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED(1001, "You do not have permission to perform this action.", HttpStatus.UNAUTHORIZED),

    USER_NOT_FOUND(2001, "User not found", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS(2002, "Invalid credentials", HttpStatus.UNAUTHORIZED),
    USERNAME_ALREADY_EXISTS(2003, "Username already exists", HttpStatus.BAD_REQUEST),
    USER_EXISTED(2004, "User already existed", HttpStatus.BAD_REQUEST),
    USER_DELETED(2005, "User is deleted", HttpStatus.BAD_REQUEST),
    USER_NOT_DELETED(2006, "User is not deleted", HttpStatus.BAD_REQUEST),
    USER_LOCKED(2007, "User is locked until %s", HttpStatus.BAD_REQUEST),
    USER_NOT_LOCKED(2008, "User is not locked", HttpStatus.BAD_REQUEST),

    PERMISSION_NOT_FOUND(3001, "Permission not found", HttpStatus.NOT_FOUND),

    CANNOT_DELETE_SUPER_ADMIN(4001, "Cannot delete super admin", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_OTHER_ADMIN(4002, "Cannot delete other admin", HttpStatus.BAD_REQUEST),
    CANNOT_UPDATE_SUPER_ADMIN(4003, "Cannot update super admin", HttpStatus.BAD_REQUEST),
    CANNOT_UPDATE_OTHER_ADMIN(4004, "Cannot update other admin", HttpStatus.BAD_REQUEST),

    INVALID_TOKEN(5001, "Invalid token", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED(5002, "Token expired", HttpStatus.UNAUTHORIZED),
    LOGIN_LOG_NOT_FOUND(5003, "Login log not found", HttpStatus.NOT_FOUND),
    ;
    int code;
    String message;
    HttpStatusCode statusCode;

}
