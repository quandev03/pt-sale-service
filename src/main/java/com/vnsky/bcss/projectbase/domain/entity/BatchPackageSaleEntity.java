package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "BATCH_PACKAGE_SALE")
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class BatchPackageSaleEntity {
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

    @Column(name = "TYPE")
    private Integer type;

    @Column(name = "ORDER_ID")
    private String orderId;

    @CreatedBy
    @Column(name = "CREATED_BY", nullable = false, updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "FINISHED_DATE")
    private LocalDateTime finishedDate;

}
