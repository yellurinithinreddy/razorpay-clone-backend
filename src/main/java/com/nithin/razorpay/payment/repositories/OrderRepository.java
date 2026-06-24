package com.nithin.razorpay.payment.repositories;

import com.nithin.razorpay.payment.entities.OrderRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderRecord, UUID> {

    boolean existsByMerchantIdAndReceipt(UUID merchantId,String receipt);


    Optional<OrderRecord> findByIdAndMerchantId(UUID orderId, UUID merchantId);
}
