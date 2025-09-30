package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ESIM_REGISTRATION")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class EsimRegistrationEntity extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private String id;

    @Column(name = "ORDER_ID")
    private String orderId;

    @Column(name = "FINISHED_DATE")
    private LocalDateTime finishedDate;

    @Column(name = "SUCCESSED_NUMBER")
    private Integer successedNumber;

    @Column(name = "FAILED_NUMBER")
    private Integer failedNumber;

    @Column(name = "BOOK_ESIM_STATUS")
    private Integer bookEsimStatus;
}
