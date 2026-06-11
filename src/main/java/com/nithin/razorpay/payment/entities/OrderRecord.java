package com.nithin.razorpay.payment.entities;

import com.nithin.razorpay.common.entities.Money;
import com.nithin.razorpay.common.enums.OrderStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "order_record")
public class OrderRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    //no FK cross service boundary
    @Column(nullable = false)
    private UUID merchant_id;

    @Embedded
    private Money amount;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.CREATED;

    @Column(nullable = false)
    private Integer attempts = 0;

    @Column(columnDefinition = "jsonb") // columnDefinition tells sql which datatype to use while triggering ddl command create
    @JdbcTypeCode(SqlTypes.JSON) // this tells that the json is converted to map when we want to use in java code otherwise we will get string
    private Map<String,Object> notes;

    @Column(nullable = false)
    private LocalDateTime expiresAt;






}
