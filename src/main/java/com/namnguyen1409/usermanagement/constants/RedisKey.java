package com.namnguyen1409.usermanagement.constants;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class RedisKey {
    public static final String USER_NAME_SET = "user:username";
    public static final String USER_EMAIL_SET = "user:email";
    public static final String USER_PHONE_SET = "user:phone";
    public static final String ACCESS_TOKEN_BLACKLIST_SET = "token:blacklist:access";
    public static final String REFRESH_TOKEN_SET = "token:refresh";

    private RedisKey() {

    }
}
