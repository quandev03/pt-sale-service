package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "ORGANIZATION_USER")
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class OrganizationUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "ORG_ID")
    private String orgId;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "IS_CURRENT")
    private Integer isCurrent;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "USER_FULLNAME")
    private String userFullname;

    @Column(name = "CLIENT_ID")
    private String clientId;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "EMAIL")
    private String email;
}
