package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "ORGANIZATION_UNIT_IMAGE")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrganizationUnitImageEntity extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "ORG_UNIT_ID", nullable = false)
    private String orgUnitId;

    @Column(name = "IMAGE_URL", nullable = false, length = 512)
    private String imageUrl;

    @Column(name = "FILE_NAME", length = 255)
    private String fileName;

    @Column(name = "FILE_SIZE")
    private Long fileSize;

    @Column(name = "CONTENT_TYPE", length = 100)
    private String contentType;

    @Column(name = "SORT_ORDER")
    private Integer sortOrder;
}


