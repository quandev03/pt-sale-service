package com.vnsky.bcss.projectbase.domain.entity;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomServiceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "ROOM_PAYMENT_DETAIL")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoomPaymentDetailEntity extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "ROOM_PAYMENT_ID", nullable = false)
    private String roomPaymentId;

    @Column(name = "ROOM_SERVICE_ID")
    private String roomServiceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "SERVICE_TYPE", nullable = false, length = 50)
    private RoomServiceType serviceType;

    @Column(name = "SERVICE_NAME", nullable = false, length = 255)
    private String serviceName;

    @Column(name = "QUANTITY", precision = 18, scale = 2)
    private BigDecimal quantity;

    @Column(name = "UNIT_PRICE", nullable = false, precision = 18, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "AMOUNT", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;
}

