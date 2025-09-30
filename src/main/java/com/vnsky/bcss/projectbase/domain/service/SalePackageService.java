package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.MbfIsdnPckRequestDTO;
import com.vnsky.bcss.projectbase.domain.dto.PackageProfileDTO;
import com.vnsky.bcss.projectbase.domain.dto.ExcelSalePackage;
import com.vnsky.bcss.projectbase.domain.dto.SalePackageDTO;
import com.vnsky.bcss.projectbase.domain.dto.PackageResponse;
import com.vnsky.bcss.projectbase.domain.port.primary.SalePackageServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.PackageProfileRepoPort;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.EnumSalePackage;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.security.SecurityUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalePackageService implements SalePackageServicePort {

    private final ServicePackageFactory servicePackageFactory;
    private final PackageProfileRepoPort packageProfileRepoPort;

    @Override
    public List<PackageResponse> checkIsdn(String isdn, Integer type) {
        try {
            if (Objects.equals(type, EnumSalePackage.PARTNER.getValue())) {
                // Check ISDN for partner
                log.debug("Checking ISDN for partner: {}", isdn);
            }
            // Validate SIM status for active
            validateSimStatusForActive(isdn);
        } catch (BaseException e) {
            throw BaseException.badRequest(ErrorCode.INVALID_INPUT)
                .addParameter("message", Objects.requireNonNull(e.problemDetail().getDetail()))
                .build();
        }

        MbfIsdnPckRequestDTO req = new MbfIsdnPckRequestDTO(isdn);

        // Call external service to get packages
        MbfPckIsdnResponseDTO mbfResponse = callPackageHubService(req);

        log.info("Check response: {}", mbfResponse);

        if (!mbfResponse.getCode().equals("00")) { // SUCCESS code
            throw BaseException.badRequest(ErrorCode.INVALID_INPUT)
                .addParameter("message", mbfResponse.getMessage())
                .build();
        }

        // Get authorized packages for current partner
        String currentClientId = SecurityUtil.getCurrentClientId();
        log.info("Current client id: {}", currentClientId);
        Set<String> authorizedPck = packageProfileRepoPort.getListPackageProfile(currentClientId)
            .stream()
            .map(PackageProfileDTO::getPckCode)
            .collect(Collectors.toSet());

        // Build package response list
        List<PackageResponse> packageResponses = new ArrayList<>();
        for (MbfPckDetailResponseDTO mbfPackage : mbfResponse.getData()) {
            if (!authorizedPck.contains(mbfPackage.getPckName())) {
                continue;
            }

            PackageResponse packageResponse = PackageResponse.builder()
                .packageId(mbfPackage.getId())
                .packageCode(mbfPackage.getPckName())
                .build();
            packageResponses.add(packageResponse);
        }
        return packageResponses;
    }

    @Override
    @Transactional
    public Object registerPackage(SalePackageDTO salePackage) {
        BasePackageServicePort servicePort = this.servicePackageFactory.getService(EnumSalePackage.PARTNER);
        return servicePort.registerPackage(salePackage);
    }

    @Override
    public List<ExcelSalePackage> checkData(MultipartFile attachment) {
        BasePackageServicePort servicePort = this.servicePackageFactory.getService(EnumSalePackage.PARTNER);
        return servicePort.checkData(attachment);
    }

    @Override
    public Object submitData(MultipartFile attachment) {
        BasePackageServicePort servicePort = this.servicePackageFactory.getService(EnumSalePackage.PARTNER);
        return servicePort.submitData(attachment);
    }

    // Helper methods
    private void validateSimStatusForActive(String isdn) {
        // Validate SIM status for activation
        log.debug("Validating SIM status for ISDN: {}", isdn);
        // Implementation would call external service
    }

    private MbfPckIsdnResponseDTO callPackageHubService(MbfIsdnPckRequestDTO request) {
        // Simulate calling external package hub service
        log.debug("Calling package hub service for ISDN: {}", request.getIsdn());

        // Create mock response
        MbfPckIsdnResponseDTO response = new MbfPckIsdnResponseDTO();
        response.setCode("00");
        response.setMessage("Success");

        // Add some mock packages
        List<MbfPckDetailResponseDTO> packages = new ArrayList<>();

        MbfPckDetailResponseDTO package1 = new MbfPckDetailResponseDTO();
        package1.setId("pkg-001");
        package1.setPckName("DATA30");
        packages.add(package1);

        MbfPckDetailResponseDTO package2 = new MbfPckDetailResponseDTO();
        package2.setId("pkg-002");
        package2.setPckName("VOICE60");
        packages.add(package2);

        response.setData(packages);
        return response;
    }

    // Mock DTOs for external service responses
    @Setter
    @Getter
    public static class MbfPckIsdnResponseDTO {
        private String code;
        private String message;
        private List<MbfPckDetailResponseDTO> data;

        @Override
        public String toString() {
            return "MbfPckIsdnResponseDTO{code='" + code + "', message='" + message + "'}";
        }
    }

    @Setter
    @Getter
    public static class MbfPckDetailResponseDTO {
        private String id;
        private String pckName;

    }
}
