package com.nithin.razorpay.merchant.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "merchant_id",nullable = false)
    private Merchant merchant;

    @Column(nullable = false,length = 50)
    private String name;

    @Column(length = 200,nullable = false)
    @Email(message = "please enter a valid email")
    private String email;

    @Column(nullable = false,length = 20)
    private String contactNumber;

    private LocalDateTime deletedAt;


}
