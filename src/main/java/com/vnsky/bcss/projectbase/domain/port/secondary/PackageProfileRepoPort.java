package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.PackageProfileDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PackageProfileRepoPort {
    boolean isExistPackageCode(String packageCode);

    PackageProfileDTO saveAndFlush(PackageProfileDTO dto);

    PackageProfileDTO findById(String idPackageProfile);
    PackageProfileDTO findByPackageCode(String packageCode);

    void deleteById(String idPackageProfile);

    List<PackageProfileDTO> getListPackageProfileFree();

    Page<PackageProfileDTO> searchPackageProfile(String pckCodeOrPckName, Integer status, Long minPrice, Long maxPrice, Pageable pageable);

    Optional<PackageProfileDTO> findByPckCode(String pckCode);

    List<PackageProfileDTO> getAll();
}
