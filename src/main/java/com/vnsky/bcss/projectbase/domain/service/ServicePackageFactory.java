package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.EnumSalePackage;
import org.springframework.stereotype.Component;

@Component
public class ServicePackageFactory {

    private final PartnerPackageService partnerPackageService;

    public ServicePackageFactory(PartnerPackageService partnerPackageService) {
        this.partnerPackageService = partnerPackageService;
    }

    public BasePackageServicePort getService(EnumSalePackage type) {
        return switch (type) {
            case PARTNER -> partnerPackageService;
            default -> throw new IllegalArgumentException("Loại phiếu không hợp lệ: " + type);
        };
    }
}
