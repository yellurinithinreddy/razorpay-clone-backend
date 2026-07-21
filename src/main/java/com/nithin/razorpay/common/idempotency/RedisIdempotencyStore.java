package com.nithin.razorpay.common.idempotency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisIdempotencyStore implements IdempotencyStore{

    private final StringRedisTemplate redis;

    private final String PREFIX = "api-key:";

    @Override
    public boolean setIfAbsent(String key, Duration ttl) {
        try{
            Boolean set = redis.opsForValue().setIfAbsent(PREFIX+key,IN_PROGRESS,ttl);
            return Boolean.TRUE.equals(set);
        }catch(DataAccessException ex){
            log.warn("");
            return true;
        }
    }

    @Override
    public void store(String key, String value, Duration ttl) {
        try{
            redis.opsForValue().set(PREFIX+key,value,ttl);
        }catch(DataAccessException ex){
            log.warn("");
        }
    }

    @Override
    public Optional<String> get(String key) {
        try{
            return Optional.ofNullable(redis.opsForValue().get(PREFIX+key));
        }catch(DataAccessException ex){
            log.warn("");
            return null;
        }
    }

    @Override
    public void delete(String key) {
        try{
            redis.opsForValue().getAndDelete(PREFIX+key);
        }catch(DataAccessException ex){
            log.warn("");
        }
    }
}
