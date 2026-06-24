package com.nithin.razorpay.payment.entities;

import com.nithin.razorpay.common.entities.BaseEntity;
import com.nithin.razorpay.common.entities.Money;
import com.nithin.razorpay.common.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "order_record",
    indexes = {
        @Index(name = "idx_order_id_merchant_id",columnList = "id, merchant_id"),
        @Index(name = "idx_order_merchant_id",columnList = "merchant_id")
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    //no FK cross service boundary
    @Column(nullable = false)
    private UUID merchantId;

    @Embedded
    private Money amount;

    @Column(length = 100)
    private String receipt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.CREATED;

    @Column(nullable = false)
    @Builder.Default
    private Integer attempts = 0;

    @Column(columnDefinition = "jsonb") // columnDefinition tells sql which datatype to use while triggering ddl command create
    @JdbcTypeCode(SqlTypes.JSON) // this tells that the json is converted to map when we want to use in java code otherwise we will get string
    private Map<String,Object> notes;

    @Column(nullable = false)
    private LocalDateTime expiresAt;






}
