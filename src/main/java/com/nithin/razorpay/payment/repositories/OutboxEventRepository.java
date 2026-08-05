package com.nithin.razorpay.payment.repositories;

import com.nithin.razorpay.common.enums.OutboxStatus;
import com.nithin.razorpay.payment.entities.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findByStatusOrderByCreatedAt(OutboxStatus status);
}
