package com.vnsky.bcss.projectbase.domain.entity;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.AdvertisementStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "ADVERTISEMENT")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdvertisementEntity extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "TITLE", nullable = false, length = 255)
    private String title;

    @Column(name = "CONTENT", columnDefinition = "CLOB")
    private String content;

    @Column(name = "IMAGE_URL", length = 512)
    private String imageUrl;

    @Column(name = "START_DATE", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "END_DATE", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 50)
    private AdvertisementStatus status;

    @Column(name = "CLIENT_ID", nullable = false)
    private String clientId;

    @Column(name = "VIEW_COUNT", nullable = false)
    private Long viewCount = 0L;
}


