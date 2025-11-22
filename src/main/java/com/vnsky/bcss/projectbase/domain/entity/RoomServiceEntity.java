package com.vnsky.bcss.projectbase.domain.entity;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomServiceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "ROOM_SERVICE")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoomServiceEntity extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "ORG_UNIT_ID", nullable = false)
    private String orgUnitId;

    @Enumerated(EnumType.STRING)
    @Column(name = "SERVICE_TYPE", nullable = false, length = 50)
    private RoomServiceType serviceType;

    @Column(name = "SERVICE_CODE", nullable = false, length = 100)
    private String serviceCode;

    @Column(name = "SERVICE_NAME", nullable = false, length = 255)
    private String serviceName;

    @Column(name = "PRICE", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "CLIENT_ID", nullable = false)
    private String clientId;

    @Column(name = "STATUS", nullable = false)
    private Integer status;
}

