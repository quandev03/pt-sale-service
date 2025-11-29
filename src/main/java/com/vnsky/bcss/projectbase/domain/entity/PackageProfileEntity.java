package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "PACKAGE_PROFILE")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class PackageProfileEntity extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "PCK_CODE")
    private String pckCode;

    @Column(name = "PCK_NAME")
    private String pckName;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "PCK_PRICE")
    private Long packagePrice;

    @Column(name = "URL_IMAGE_PCK")
    private String urlImagePackage;

    @Column(name = "CYCLE_VALUE")
    private Integer cycleValue;

    @Column(name = "CYCLE_UNIT")
    private Integer cycleUnit;

    @Column(name = "TYPE_SERVICE")
    private String packageType;
}
