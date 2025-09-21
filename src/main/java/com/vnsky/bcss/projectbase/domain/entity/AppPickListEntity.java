package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "APP_PICK_LIST")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class AppPickListEntity extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "TABLE_NAME")
    private String tableName;

    @Column(name = "COLUMN_NAME")
    private String columnName;

    @Column(name = "CODE")
    private String code;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "VALUE_TYPE")
    private String valueType;

    @Column(name = "REF_ID")
    private Long refId;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "IS_DEFAULT")
    private Integer isDefault;
} 