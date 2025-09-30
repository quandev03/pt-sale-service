package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.PackageProfileDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.response.StatisticOrgResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.StatisticResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PackageManagerServicePort {
    Page<PackageProfileDTO> getListPackageProfile(String pckCodeOrPckName, Integer status, Long minPrice, Long maxPrice, Pageable pageable);

    PackageProfileDTO getDetailPackageProfile(String idPackageProfile);

    PackageProfileDTO createPackageProfile(PackageProfileDTO data, MultipartFile imagePackage);

    PackageProfileDTO updatePackageProfile(String idPackageProfile,  PackageProfileDTO data,MultipartFile images);
    PackageProfileDTO updateStatusPackageProfile(String idPackageProfile, int status);

    void deletePackageProfile(String idPackageProfile);

    List<PackageProfileDTO> getListPackageProfile();

    List<PackageProfileDTO> getAllPackageProfile();

    ResponseEntity<Resource> downloadImage(String idPackageProfile);

    Long totalPackagesSold(String orgCode);

    List<StatisticResponse> statisticPackagesSold(String orgCode, String startDate, String endDate, int granularity);

    List<StatisticOrgResponse> statisticPackagesSoldOrg(String orgCode, String startDate, String endDate);

    Long revenusPackageSold(String orgCode);
}
