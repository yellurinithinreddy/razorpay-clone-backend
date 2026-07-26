package com.nithin.razorpay.merchant.entities;

import com.nithin.razorpay.common.entities.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer",
    indexes = {
        @Index(name = "idx_customer_merchant_id",columnList = "merchant_id"),
        @Index(name = "idx_customer",columnList = "email")
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "merchant_id",nullable = false)
    private Merchant merchant;

    @Column(length = 50)
    private String name;

    @Column(length = 200)
    @Email(message = "please enter a valid email")
    private String email;

    @Column(length = 20)
    private String phone;

    private LocalDateTime deletedAt;


}
