package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "ISDN_TRANSACTION")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class IsdnTransactionEntity extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "TRANS_DATE")
    private LocalDateTime transDate;

    @Column(name = "TRANS_STATUS")
    private Integer transStatus;

    @Column(name = "APPROVAL_STATUS")
    private Integer approvalStatus;

    @Column(name = "REASON_ID")
    private String reasonId;

    @Column(name = "PROCESS_TYPE")
    private Integer processType;

    @Column(name = "TOTAL_NUMBER")
    private Long totalNumber;

    @Column(name = "FAILED_NUMBER")
    private Long failedNumber;

    @Column(name = "SUCCEEDED_NUMBER")
    private Long succeededNumber;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "UPLOAD_STATUS")
    private Integer uploadStatus;

    @Column(name = "VALID_FAILED_NUMBER")
    private Long validFailedNumber;

    @Column(name = "VALID_SUCCEEDED_NUMBER")
    private Long validSucceededNumber;

    @Column(name = "UPLOAD_FILENAME")
    private String uploadFilename;

    @Column(name = "STEP_STATUS")
    private Long stepStatus;

    @Column(name = "ORDER_ID")
    private String orderId;

    @Column(name = "METADATA")
    private String metadata;

    @Column(name = "CLIENT_ID")
    private String clientId;
}
