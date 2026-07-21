package com.nithin.razorpay.common.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

//@Component
@RequiredArgsConstructor
public class SlidingWindowRateLimiter implements RateLimiter{
    private final StringRedisTemplate redis;

    @Override
    public RateLimitResult check(String key, int maxRequestAllowed, long windowSeconds) {

        String redisKey = "ratelimit:sliding"+key;
        long nowMs = System.currentTimeMillis();
        long floorMs = nowMs - windowSeconds*1000;

        var zset = redis.opsForZSet();

        zset.removeRangeByScore(redisKey,Double.NEGATIVE_INFINITY,floorMs);

        Long count = zset.zCard(redisKey);

        count = count != null ? count : 0;

        if(count >= maxRequestAllowed){
            var oldest = zset.rangeWithScores(redisKey,0,0);
            int retryAfter = 1;

            if(oldest != null && !oldest.isEmpty()){
                Double oldestScore = oldest.iterator().next().getScore();
                if(oldestScore != null){
                    long windowExpireMs = oldestScore.longValue()+windowSeconds*1000;
                    retryAfter = (int) ((windowExpireMs-nowMs)/1000.0);
                }

            }
            return RateLimitResult.denied(retryAfter);
        }

        zset.add(redisKey, UUID.randomUUID().toString(),nowMs);
        redis.expire(redisKey, Duration.ofSeconds(windowSeconds+1));



        return RateLimitResult.allowed((int) (maxRequestAllowed-count-1));
    }
}
