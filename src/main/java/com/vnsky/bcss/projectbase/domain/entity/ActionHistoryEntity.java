package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.time.LocalDateTime;

@Entity
@Table(name = "ACTION_HISTORY")
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class ActionHistoryEntity  {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "SUB_ID")
    private String subId;

    @Column(name = "ACTION_DATE")
    private LocalDateTime actionDate;

    @Column(name = "ACTION_CODE")
    private String actionCode;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "SHOP_CODE")
    private String shopCode;

    @Column(name = "EMP_CODE")
    private String empCode;

    @Column(name = "EMP_NAME")
    private String empName;

    @Column(name = "REASON_CODE")
    private String reasonCode;

    @Column(name = "REASON_NOTE")
    private String reasonNote;

    @Serial
    private static final long serialVersionUID = 1L;

    @CreatedBy
    @Column(name = "CREATED_BY", nullable = false, updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE", updatable = false)
    private LocalDateTime createdDate;
}
