package com.nithin.razorpay.merchant.entities;

import com.nithin.razorpay.common.enums.BusinessType;
import com.nithin.razorpay.common.enums.MerchantStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import java.util.UUID;

@Entity
@Table(name = "merchant")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Email(message = "please enter a valid email")
    @Column(unique = true,nullable = false)
    private String email;

    @Column(nullable = false,length = 20)
    private String contactNumber;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private BusinessType businessType;

    @Column(length = 100)
    private String businessName;

    @Column(length = 200)
    private String websiteUrl;


    @Column(nullable = false,length = 200)
    @Enumerated(EnumType.STRING)
    private MerchantStatus status = MerchantStatus.PENDING_KYC;

    @Column(length = 20)
    private String gstId;

    @Column(length = 20)
    private String panId;

    @Column(length = 200)
    private String settlementBankAccount;

    @Column(length = 20)
    private String settlementBankIfsc;
    @Column(length = 200)
    private String settlementBankAccountHolderName;
}
