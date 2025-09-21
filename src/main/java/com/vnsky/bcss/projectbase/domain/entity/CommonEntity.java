package com.vnsky.bcss.projectbase.domain.entity;

import com.vnsky.security.SecurityUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class CommonEntity implements Serializable {
    @Size(max = 50)
    @NotNull
    @Column(name = "CREATED_BY", nullable = false, length = 50)
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @Size(max = 50)
    @NotNull
    @Column(name = "MODIFIED_BY", nullable = false, length = 50)
    private String modifiedBy;

    @LastModifiedDate
    @Column(name = "MODIFIED_DATE", nullable = false)
    private LocalDateTime modifiedDate;

    @PreUpdate
    public void onPreUpdate() {
        setModifiedBy(SecurityUtil.getCurrentUsername());
    }

    @PrePersist
    public void onPrePersist() {
        setCreatedBy(SecurityUtil.getCurrentUsername());
        setModifiedBy(SecurityUtil.getCurrentUsername());
    }
}
