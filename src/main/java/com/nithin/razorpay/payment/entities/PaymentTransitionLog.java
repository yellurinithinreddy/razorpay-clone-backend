package com.nithin.razorpay.payment.entities;

import com.nithin.razorpay.common.entities.BaseEntity;
import com.nithin.razorpay.common.enums.PaymentActor;
import com.nithin.razorpay.common.enums.PaymentEvent;
import com.nithin.razorpay.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_transition_log",
    indexes = {
        @Index(name = "idx_payment_transition_log_payment_id",columnList = "payment_id")
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentTransitionLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(nullable = false,name = "payment_id")
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(length = 30,nullable = false)
    private PaymentStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(length = 30,nullable = false)
    private PaymentEvent event;

    @Enumerated(EnumType.STRING)
    @Column(length = 30,nullable = false)
    private PaymentStatus toStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private PaymentActor actor;

    @Column(nullable = false)
    private LocalDateTime occuredAt;


}
