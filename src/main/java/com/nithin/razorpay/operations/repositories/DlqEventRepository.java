package com.nithin.razorpay.operations.repositories;

import com.nithin.razorpay.operations.entities.DlqEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DlqEventRepository extends JpaRepository<DlqEvent, UUID> {
}
