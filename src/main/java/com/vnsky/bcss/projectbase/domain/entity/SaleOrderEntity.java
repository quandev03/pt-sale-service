package com.vnsky.bcss.projectbase.domain.entity;

import com.vnsky.bcss.projectbase.shared.utils.SaleOrderNoSequence;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "SALE_ORDER")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SaleOrderEntity extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "ORG_ID")
    private String orgId;

    @Column(name = "ORDER_NO")
    @SaleOrderNoSequence
    private String orderNo;

    @Column(name = "AMOUNT_TOTAL")
    private BigDecimal amountTotal;

    @Column(name = "BOOK_ESIM_STATUS")
    private Integer status;

    @Column(name = "REASON_ID")
    private String reasonId;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "ORDER_TYPE")
    private Integer orderType;

    @Column(name = "CUSTOMER_EMAIL")
    private String customerEmail;

    @Column(name = "QUANTITY")
    private BigDecimal quantity;

    @Column(name = "CANCEL_REASON")
    private String cancelReason;

    @Column(name = "SUCCEEDED_NUMBER")
    private Integer succeededNumber;

    @Column(name = "FAILED_NUMBER")
    private Integer failedNumber;

    @Column(name = "FINISHED_DATE")
    private LocalDateTime finishedDate;

    @Column(name = "PCK_CODE")
    private String pckCode;

    @Column(name = "ORDER_DATE")
    private LocalDateTime orderDate;
}
