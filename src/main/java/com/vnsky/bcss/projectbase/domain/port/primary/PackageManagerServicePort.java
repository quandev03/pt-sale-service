package com.vnsky.bcss.projectbase.domain.port.primary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vnsky.bcss.projectbase.domain.dto.PackageProfileDTO;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PackageManagerServicePort {
    Page<PackageProfileDTO> getListPackageProfile(String pckCodeOrPckName, Integer status, Long minPrice, Long maxPrice, Pageable pageable);

    PackageProfileDTO getDetailPackageProfile(String idPackageProfile);

    PackageProfileDTO createPackageProfile(PackageProfileDTO data, MultipartFile imagePackage);

    PackageProfileDTO updatePackageProfile(String idPackageProfile,  PackageProfileDTO data,MultipartFile images);
    PackageProfileDTO updateStatusPackageProfile(String idPackageProfile, int status);

    void deletePackageProfile(String idPackageProfile);

    List<PackageProfileDTO> getListPackageProfileFree();

    List<PackageProfileDTO> getAllPackageProfile();
}
