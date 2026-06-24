package com.nithin.razorpay.merchant.entities;

import com.nithin.razorpay.common.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "merchant_webhook_config",
    indexes = {
        @Index(name = "idx_webhook_merchant_id",columnList = "merchant_id, enabled")
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MerchantWebhookConfig extends BaseEntity {

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
