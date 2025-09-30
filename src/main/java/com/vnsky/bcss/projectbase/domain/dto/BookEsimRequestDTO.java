package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BookEsimRequestDTO extends AbstractAuditDTO {
    @DbColumnMapper("ID")
    private String id;

    @DbColumnMapper("ESIM_REGISTRATION_ID")
    private String esimRegistrationId;

    @DbColumnMapper("QUANTITY")
    private Long quantity;

    @DbColumnMapper("PCK_CODE")
    private String pckCode;
}
