package com.vnsky.bcss.projectbase.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnsky.bcss.projectbase.domain.dto.*;
import com.vnsky.bcss.projectbase.domain.port.primary.OrganizationPartnerServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUnitRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.PackageClientRepoPost;
import com.vnsky.bcss.projectbase.domain.port.secondary.PackageProfileRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.external.IntegrationPort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.AgentDebitRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.PackageClientRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.BaseIntegrationRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.AgentDebitResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.GetAllOrganizationUnitResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SearchPartnerResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.external.MbfPartnerInfoResponse;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.constant.IntegrationConstant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.utils.StringUtilsOCR;
import com.vnsky.common.exception.domain.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationPartnerService implements OrganizationPartnerServicePort {

    private final OrganizationUnitRepoPort organizationUnitRepositoryPort;
    private final PackageClientRepoPost packageClientRepoPost;
    private final PackageProfileRepoPort packageProfileRepoPort;
    private final IntegrationPort integrationPort;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String URL_CREATE_CLIENT = "/internal/api/clients";

    @Value("${third-party.admin.apikey}")
    private String apiKey;

    @Value("${domain.internal.domain-admin}")
    private String domainAdmin;

    private static final Integer FIRST_OBJECT = 0;
    private static final String CMD = "MBF";
    private static final String TYPE = "AGENT_DEBIT";

    @Override
    @Transactional
    public OrganizationUnitDTO createPartner(OrganizationUnitDTO organizationUnitDTO, String lang) {
        this.validCodePartner(organizationUnitDTO);

        ClientDTO clientDTO = ClientDTO.builder()
            .name(organizationUnitDTO.getOrgName())
            .code(organizationUnitDTO.getOrgCode())
            .contactName(organizationUnitDTO.getRepresentative())
            .contactPhone(organizationUnitDTO.getPhone())
            .permanentAddress(organizationUnitDTO.getAddress())
            .permanentProvinceId(organizationUnitDTO.getProvinceCode())
            .parentCode(organizationUnitDTO.getParentCode())
            .employeeCode(organizationUnitDTO.getEmployeeCode())
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("x-api-key", apiKey);
        headers.add(HttpHeaders.ACCEPT_LANGUAGE, lang);

        String urlCreateClient = domainAdmin + URL_CREATE_CLIENT;

        HttpEntity<Object> requestEntityCreateClient = new HttpEntity<>(clientDTO, headers);

        // Gọi API tạo Client
        log.info("Bat dau call api create client");
        ResponseEntity<ClientDTO> response = restTemplate.exchange(
            urlCreateClient, HttpMethod.POST, requestEntityCreateClient, ClientDTO.class
        );

        // Kiểm tra status và body
        log.info("Bat dau kiem tra response");
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw BaseException.badRequest(ErrorCode.CREATE_CLIENT_FAILED).build();
        }

        // kiểm tra response chứa client_id
        ClientDTO createdClient = response.getBody();
        if (createdClient == null || createdClient.getId() == null) {
            throw BaseException.badRequest(ErrorCode.MISSING_CLIENT_ID).build();
        }

        organizationUnitDTO.setClientId(createdClient.getId());
        organizationUnitDTO.setStatus(Constant.Status.ACTIVE);
        organizationUnitDTO.setOrgType(Constant.OrgType.NBO);
        organizationUnitDTO.setDebtLimit(0L);
        organizationUnitDTO.setDebtLimitMbf(0L);
        this.organizationUnitRepositoryPort.save(organizationUnitDTO);

        organizationUnitDTO.setOrgType(Constant.OrgType.PARTNER);
        return this.organizationUnitRepositoryPort.save(organizationUnitDTO);
    }

    @Override
    @Transactional
    public OrganizationUnitDTO updatePartner(String id, OrganizationUnitDTO organizationUnitDTO) {
        this.validCodePartner(organizationUnitDTO);
        organizationUnitDTO.setId(id);
        return this.organizationUnitRepositoryPort.updatePartner(organizationUnitDTO);
    }

    @Override
    public OrganizationUnitDTO getDetailById(String id) {
        OrganizationUnitDTO organizationUnitDTO = this.organizationUnitRepositoryPort.findById(id)
            .orElseThrow(() -> BaseException.badRequest(ErrorCode.ORG_NOT_EXISTED).build());

        OrganizationDeliveryInfoDTO organizationDeliveryInfoDTO = this.organizationUnitRepositoryPort.findDeliveryByOrgId(id);
        organizationUnitDTO.setDeliveryInfos(Collections.singletonList(organizationDeliveryInfoDTO));

        return organizationUnitDTO;
    }

    @Override
    public Page<SearchPartnerResponse> searchPartner(String q, String partnerType, Integer status, Integer approvalStatus, Pageable pageable) {
        return this.organizationUnitRepositoryPort.searchPartner(StringUtilsOCR.buildLikeOperator(q), partnerType, status, approvalStatus, pageable);
    }

    @Override
    @Transactional
    public void updateStatusPartner(String id, Integer status) {
        this.organizationUnitRepositoryPort.updateStatus(id, status);
    }

    @Override
    @Transactional
    public List<GetAllOrganizationUnitResponse> getUnitByCode(String code) {
        OrganizationUnitDTO organizationUnitDTO = this.organizationUnitRepositoryPort.findByCodeAndType(code, Constant.OrgType.PARTNER);
        return this.organizationUnitRepositoryPort.getAllUnitByClientId(null, organizationUnitDTO.getClientId());
    }

    @Override
    public List<GetAllOrganizationUnitResponse> getAllPartner(String q) {
        return this.organizationUnitRepositoryPort.getAllOrganizationByType(StringUtilsOCR.buildLikeOperator(q), Constant.OrgType.PARTNER);
    }

    @Override
    public OrganizationDeliveryInfoDTO getDeliveryInfo(FileAttachments attachments) {
        return new OrganizationDeliveryInfoDTO();
    }

    @Override
    public OrganizationUnitDTO getDetailByCode(String code) {
        BaseIntegrationRequest integrationRequest = buildRequestForPartnerInfo(code);

        MbfPartnerInfoResponse mbfPartnerInfoResponse = integrationPort.executeRequestWithRetry(
            integrationRequest, MbfPartnerInfoResponse.class
        );

        if (mbfPartnerInfoResponse != null &&
            "SUCCESS".equalsIgnoreCase(mbfPartnerInfoResponse.getCode()) &&
            mbfPartnerInfoResponse.getData() != null &&
            !mbfPartnerInfoResponse.getData().isEmpty()) {

            // Lấy phần tử đầu tiên (do data là 1 list)
            MbfPartnerInfoResponse.MbfPartnerInfo info = mbfPartnerInfoResponse.getData().get(0);

            // Map về OrganizationUnitDTO
            OrganizationUnitDTO dto = new OrganizationUnitDTO();
            dto.setId(info.getShopId());
            dto.setPhone(info.getTelNumber());
            dto.setOrgCode(info.getShopCode());
            dto.setOrgName(info.getShopName());
            dto.setOrgType(info.getShopType());
            dto.setProvinceCode(info.getProvinceCode());
            dto.setParentId(info.getParentShopId());
            dto.setAddress(info.getShopAddress());
            dto.setStatus("1".equals(info.getShopStatus()) ? 1 : 0);
            dto.setTaxCode(info.getTaxCode());
            dto.setRepresentative(info.getContactName());
            dto.setParentCode(info.getParentShopCode());
            return dto;
        }
        throw BaseException.badRequest(ErrorCode.ORG_NOT_EXISTED).build();
    }

    @Override
    @Transactional
    public void createPackageClientForClient(PackageClientRequest clientRequest) {
        // Validate org trước
        if (organizationUnitRepositoryPort
            .getOrgByClientIdAndOrgType(clientRequest.getClientId(), Constant.OrgType.PARTNER) == null) {
            throw BaseException.badRequest(ErrorCode.ORG_NOT_EXISTED).build();
        }

        // Nếu không có mã gói → coi như xoá trắng
        List<String> codes = Optional.ofNullable(clientRequest.getPackageCodes())
            .orElse(List.of()).stream()
            .filter(s -> s != null && !s.isBlank())
            .map(String::trim)
            .distinct()
            .toList();

        // Xoá trước…
        packageClientRepoPost.deleteAllByClient(clientRequest.getClientId());

        // … rồi mới ghi (nếu có)
        if (!codes.isEmpty()) {
            // Tận dụng saveAll đã tối ưu (đã có validate thiếu mã)
            packageClientRepoPost.saveAll(clientRequest);
        }
    }

    @Override
    public List<PackageProfileDTO> getAllPackageByClientId(String clientId) {
        return packageProfileRepoPort.getPackageByClientId(clientId);
    }

    @Override
    public OrganizationUnitDTO getOrgNBOByPartner(String partnerId) {
        return organizationUnitRepositoryPort.getOrgNBOByPartner(partnerId);
    }

    private BaseIntegrationRequest buildRequestForPartnerInfo(String orgCode) {
        Map<String, String> request = new HashMap<>();
        request.put("shopCode", orgCode);

        return integrationPort.buildIntegrationRequest(
            IntegrationConstant.MBF_CMD, IntegrationConstant.PARTNER_INFO_TYPE, null, request
        );
    }

    private void validCodePartner(OrganizationUnitDTO organizationUnitDTO) {
        if (this.organizationUnitRepositoryPort.existsByCode(organizationUnitDTO.getId(), organizationUnitDTO.getOrgCode(), organizationUnitDTO.getOrgType())) {
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE).build();
        }
    }
}
