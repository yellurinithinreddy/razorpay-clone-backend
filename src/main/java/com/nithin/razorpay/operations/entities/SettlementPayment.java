package com.nithin.razorpay.operations.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "settlement_payment")
public class SettlementPayment {

    @EmbeddedId
    private SettlementPaymentId settlementPaymentId;

    @MapsId("settlementId")
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "settlement_id",nullable = false)
    private Settlement settlement;
}
