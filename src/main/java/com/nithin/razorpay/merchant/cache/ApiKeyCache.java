package com.nithin.razorpay.merchant.cache;

import java.util.Optional;


public interface ApiKeyCache {

    Optional<ApiKeyCacheEntry> get(String keyId);

    void put(String keyId,ApiKeyCacheEntry apiKeyEntry);

    void evict(String keyId);
}
