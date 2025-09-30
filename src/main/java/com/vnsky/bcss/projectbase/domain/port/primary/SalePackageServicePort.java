package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.ExcelSalePackage;
import com.vnsky.bcss.projectbase.domain.dto.SalePackageDTO;
import com.vnsky.bcss.projectbase.domain.dto.PackageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SalePackageServicePort {
    List<PackageResponse> checkIsdn(String isdn, Integer type);

    Object registerPackage(SalePackageDTO salePackage);

    List<ExcelSalePackage> checkData(MultipartFile attachment);

    Object submitData(MultipartFile attachment);
}
