package com.example.PetApp.redis.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    public void createData(String key, String value, long duration) {
        ValueOperations<String, String> stringStringValueOperations = redisTemplate.opsForValue();
        Duration expire = Duration.ofSeconds(duration);
        stringStringValueOperations.set(key, value, expire);
    }

    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean existData(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

}
