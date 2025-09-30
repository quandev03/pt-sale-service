package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

@Entity
@Table(name = "SALE_ORDER_LINE")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SaleOrderLineEntity extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "SALE_ORDER_ID")
    private String saleOrderId;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "VERSION")
    private Long version;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "ISDN")
    private String isdn;

    @Column(name = "PCK_CODE")
    private String pckCode;

    @Column(name = "QUANTITY")
    private Long quantity = 1L;

    @Column(name = "PAY_STATUS")
    private Integer payStatus;
}
