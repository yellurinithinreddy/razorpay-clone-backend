package com.nithin.razorpay.vault.repositories;


import com.nithin.razorpay.vault.entities.VaultCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VaultCardRepository extends JpaRepository<VaultCard, UUID> {
}
