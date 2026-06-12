package com.nithin.razorpay.operations.entities;

import com.nithin.razorpay.common.entities.Money;
import com.nithin.razorpay.common.enums.SettlementStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "settlement")
public class Settlement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID merchantId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "grossAmountUnits")),
            @AttributeOverride(name = "currency", column = @Column(name = "grossAmountCurrency"))
    })
    private Money grossAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "refundAmountUnits")),
            @AttributeOverride(name = "currency", column = @Column(name = "refundAmountCurrency"))
    })
    private Money refundAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "feeAmountUnits")),
            @AttributeOverride(name = "currency", column = @Column(name = "feeAmountCurrency"))
    })
    private Money feeAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "gstAmountUnits")),
            @AttributeOverride(name = "currency", column = @Column(name = "gstAmountCurrency"))
    })
    private Money gstAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "netAmountUnits")),
            @AttributeOverride(name = "currency", column = @Column(name = "netAmountCurrency"))
    })
    private Money netAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private SettlementStatus status;

    @Column(nullable = false,length = 20)
    private String bankReference;

    private LocalDateTime processedAt;






}
