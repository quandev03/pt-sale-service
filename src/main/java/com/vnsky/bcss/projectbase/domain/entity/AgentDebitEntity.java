package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "AGENT_DEBIT")
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class AgentDebitEntity extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "ORG_ID")
    private String orgId;

    @Column(name = "CLIENT_ID")
    private String clientId;

    @Column(name = "AMOUNT")
    private Long amount;

    @Column(name = "DEBT_LIMIT")
    private Long debtLimit;

    @Column(name = "DEBT_LIMIT_MBF")
    private Long debtLimitMbf;

    @Column(name = "PAYMENT_ID")
    private String paymentId;

    @Column(name = "TYPE")
    private String type;
}
