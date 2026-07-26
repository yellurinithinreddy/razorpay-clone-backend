package com.nithin.razorpay.merchant.repositories;

import com.nithin.razorpay.merchant.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByMerchant_IdAndEmail(UUID merchantId, String email);
}
