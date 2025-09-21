package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.ActionHistoryDTO;
import com.vnsky.bcss.projectbase.domain.dto.EsimInforDTO;
import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.SendQrCodeRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationUnitResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.ESimDetailResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EsimManagerServicePort {

    Page<EsimInforDTO> getListEsimInforPartnerDTO(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, String orgId, Pageable pageable);

    List<ActionHistoryDTO> getActionHistoryDTO(String id);

    void sendMailEsim(SendQrCodeRequest request);

    Page<EsimInforDTO> getListEsimInforDTO(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, String orgId, Pageable pageable);

    List<OrganizationUnitResponse> getListOrganization();

    Resource esimGenerateQR(String subId, String size );

    ESimDetailResponse detailEsim(String subId);

    Resource esimGenerateQRCode(String data, String size);

    List<OrganizationUnitResponse> getListOrganizationUnit();

    Resource exportListEsimExcel(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, String orgId);

    Resource exportListEsimExcelInternal(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, String orgId);
}
