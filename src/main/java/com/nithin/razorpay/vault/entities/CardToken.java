package com.nithin.razorpay.vault.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "card_token")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 50,nullable = false,unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(nullable = false,name = "vault_card_id")
    private VaultCard vaultCard;

    @Column(nullable = false)
    private UUID merchant;

    @Column(nullable = false)
    private UUID customer;

    private LocalDateTime revokedAt;
}
