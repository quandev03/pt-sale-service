package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.EsimRegistrationLineDTO;
import com.vnsky.bcss.projectbase.domain.dto.BookEsimDetailLineItemDTO;

import java.util.List;
import java.util.Optional;

public interface EsimRegistrationLineRepoPort {
    EsimRegistrationLineDTO saveAndFlush(EsimRegistrationLineDTO esimRegistrationLineDTO);
    List<EsimRegistrationLineDTO> saveAllAndFlush(List<EsimRegistrationLineDTO> esimRegistrationLineDTOs);
    Optional<EsimRegistrationLineDTO> findById(String id);
    List<BookEsimDetailLineItemDTO> findBookEsimDetailLineItemsBySaleOrderId(String saleOrderId);
    List<EsimRegistrationLineDTO> findIncompleteRegistrationLines();
    List<EsimRegistrationLineDTO> findByEsimRegistrationIdAndSerialIsNull(String esimRegistrationId);
}
