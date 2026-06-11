package com.nithin.razorpay.merchant.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "merchant_webhook_config")
public class MerchantWebhookConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(nullable = false,name="merchant_id")
    private Merchant merchant;

    @Column(length = 500,nullable = false)
    private String targetUrl;

    @Column(length = 300)
    private String webhookSecretHash;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(length = 255)
    private String eventTypes;
    //comma separated list of event types to subscribe to
}
