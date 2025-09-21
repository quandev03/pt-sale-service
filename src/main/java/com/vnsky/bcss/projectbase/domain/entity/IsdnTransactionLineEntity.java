package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "ISDN_TRANSACTION_LINE")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class IsdnTransactionLineEntity extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "TRANS_DATE")
    private LocalDate transDate;

    @Column(name = "ISDN_TRANS_ID")
    private String isdnTransId;

    @Column(name = "ISDN")
    private Long isdn;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "ERROR")
    private String error;

    @Column(name = "DESCRIPTION")
    private String description;
} 