package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "BATCH_PACKAGE_SALE")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class BatchPackageSaleEntity extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "FILE_URL")
    private String fileUrl;

    @Column(name = "RESULT_FILE_URL")
    private String resultFileUrl;

    @Column(name = "TOTAL_NUMBER")
    private Long totalNumber;

    @Column(name = "FAILED_NUMBER")
    private Long failedNumber;

    @Column(name = "SUCCEEDED_NUMBER")
    private Long succeededNumber;

    @Column(name = "PAYMENT_TYPE")
    private String paymentType;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "CLIENT_ID")
    private String clientId;
} 