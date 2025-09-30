package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "BOOK_ESIM_REQUEST")
@Entity
@EqualsAndHashCode(callSuper = true)
public class BookEsimRequestEntity extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "ESIM_REGISTRATION_ID")
    private String esimRegistrationId;

    @Column(name = "QUANTITY")
    private Long quantity;

    @Column(name = "PCK_CODE")
    private String pckCode;
}
