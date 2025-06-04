package com.namnguyen1409.usermanagement.service.cache.impl;

import com.namnguyen1409.usermanagement.constants.RedisKey;
import com.namnguyen1409.usermanagement.service.cache.UserCacheService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserCacheServiceImpl implements UserCacheService {

    private final RedisTemplate<Object, Object> redisTemplate;

    @Override
    public void addUsername(String username) {
        redisTemplate.opsForSet().add(RedisKey.USER_NAME_SET, username);
    }

    @Override
    public Boolean isUsernameExists(String username) {
        return redisTemplate.opsForSet().isMember(RedisKey.USER_NAME_SET, username);
    }

    @Override
    public void deleteUsername(String username) {
        redisTemplate.opsForSet().remove(RedisKey.USER_NAME_SET, username);
    }

    @Override
    public void addEmail(String email) {
        redisTemplate.opsForSet().add(RedisKey.USER_EMAIL_SET, email);
    }

    @Override
    public Boolean isEmailExists(String email) {
        return redisTemplate.opsForSet().isMember(RedisKey.USER_EMAIL_SET, email);
    }

    @Override
    public void deleteEmail(String email) {
        redisTemplate.opsForSet().remove(RedisKey.USER_EMAIL_SET, email);
    }

    @Override
    public void addPhone(String phone) {
        redisTemplate.opsForSet().add(RedisKey.USER_PHONE_SET, phone);
    }

    @Override
    public Boolean isPhoneExists(String phone) {
        return redisTemplate.opsForSet().isMember(RedisKey.USER_PHONE_SET, phone);
    }

    @Override
    public void deletePhone(String phone) {
        redisTemplate.opsForSet().remove(RedisKey.USER_PHONE_SET, phone);
    }


}
