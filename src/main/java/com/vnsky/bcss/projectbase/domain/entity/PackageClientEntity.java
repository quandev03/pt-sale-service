package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "PACKAGE_CLIENT")
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class PackageClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "PACKAGE_ID")
    private String packageId;

    @Column(name = "CLIENT_ID")
    private String clientId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "STATUS")
    private Integer status;
}
