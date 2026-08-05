package com.nithin.razorpay.merchant.repositories;

import com.nithin.razorpay.merchant.entities.Merchant;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, UUID> {
    boolean existsByEmail(@Email(message = "Email is required") String email);
}
