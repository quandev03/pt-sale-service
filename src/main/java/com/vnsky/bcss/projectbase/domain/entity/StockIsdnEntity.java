package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "STOCK_ISDN")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class StockIsdnEntity extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "ISDN")
    private Long isdn;

    @Column(name = "SERIAL")
    private Long serial;

    @Column(name = "IMSI")
    private Long imsi;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "ACTIVE_DATETIME")
    private LocalDateTime activeDatetime;

    @Column(name = "DELETE_DATETIME")
    private LocalDateTime deleteDatetime;

    @Column(name = "DESCRIPTION")
    private String description;
}
