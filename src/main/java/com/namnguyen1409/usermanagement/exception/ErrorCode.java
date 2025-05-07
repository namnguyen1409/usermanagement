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
    UNCATEGORIZED(500, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED(401, "You do not have permission to perform this action.", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(404, "User not found", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS(401, "Invalid credentials", HttpStatus.UNAUTHORIZED),
    USERNAME_ALREADY_EXISTS(400, "Username already exists", HttpStatus.BAD_REQUEST),
    USER_EXISTED(400, "User already existed", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_FOUND(404, "Permission not found", HttpStatus.NOT_FOUND),
    USER_DELETED(400, "User is deleted", HttpStatus.BAD_REQUEST),
    USER_NOT_DELETED(400, "User is not deleted", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_SUPER_ADMIN(400, "Cannot delete super admin", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_OTHER_ADMIN(400, "Cannot delete other admin", HttpStatus.BAD_REQUEST),
    CANNOT_UPDATE_SUPER_ADMIN(400, "Cannot update super admin", HttpStatus.BAD_REQUEST),
    CANNOT_UPDATE_OTHER_ADMIN(400, "Cannot update other admin", HttpStatus.BAD_REQUEST),
    USER_LOCKED(400, "User is locked", HttpStatus.BAD_REQUEST),
    USER_NOT_LOCKED(400, "User is not locked", HttpStatus.BAD_REQUEST),
    ;
    int code;
    String message;
    HttpStatusCode statusCode;

}
