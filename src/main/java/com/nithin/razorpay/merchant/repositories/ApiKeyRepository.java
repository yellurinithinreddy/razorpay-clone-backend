package com.nithin.razorpay.merchant.repositories;

import com.nithin.razorpay.merchant.entities.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    List<ApiKey> findByMerchant_Id(java.util.UUID merchantId);

    Optional<ApiKey> findByKeyId(String keyId);
}
