package com.nithin.razorpay.common.ratelimit;

public interface RateLimiter {

    RateLimitResult check(String key,int maxRequestAllowed, long windowSeconds);
}
