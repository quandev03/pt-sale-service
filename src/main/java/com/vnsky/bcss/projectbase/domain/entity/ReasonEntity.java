package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "REASON")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ReasonEntity extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "REASON_TYPE_ID")
    private String reasonTypeId;

    @Column(name = "REASON_CODE")
    private String reasonCode;

    @Column(name = "REASON_NAME")
    private String reasonName;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "DESCRIPTION")
    private String description;
} 