package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ROOM_PAYMENT")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoomPaymentEntity extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "ORG_UNIT_ID", nullable = false)
    private String orgUnitId;

    @Column(name = "MONTH", nullable = false)
    private Integer month;

    @Column(name = "YEAR", nullable = false)
    private Integer year;

    @Column(name = "TOTAL_AMOUNT", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "QR_CODE_URL", length = 512)
    private String qrCodeUrl;

    @Lob
    @Column(name = "QR_CODE_IMAGE")
    private byte[] qrCodeImage;

    @Column(name = "STATUS", nullable = false)
    private Integer status;

    @Column(name = "PAYMENT_DATE")
    private LocalDateTime paymentDate;
}

