package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "ESIM_REGISTRATION_LINE")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class EsimRegistrationLineEntity extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "SERIAL")
    private String serial;

    @Column(name = "ISDN")
    private Long isdn;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "IMSI")
    private Long imsi;

    @Column(name = "SALE_ORDER_ID")
    private String saleOrderId;

    @Column(name = "LPA")
    private String lpa;
} 