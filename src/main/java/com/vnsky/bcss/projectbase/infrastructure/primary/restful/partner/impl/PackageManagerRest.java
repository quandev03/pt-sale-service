package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.impl;

import com.vnsky.bcss.projectbase.domain.dto.PackageProfileDTO;
import com.vnsky.bcss.projectbase.domain.service.PackageManagerService;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.PackageManagerOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PackageManagerRest implements PackageManagerOperation {

    private final PackageManagerService packageManagerService;

    @Override
    public ResponseEntity<Object> getListPackage(String pckCodeOrPckName, Integer status, Long minPrice, Long maxPrice, Pageable pageable) {
        return ResponseEntity.ok(packageManagerService.getListPackageProfile(pckCodeOrPckName, status, minPrice, maxPrice, pageable));
    }

    @Override
    public ResponseEntity<Object> getDetailPackageProfile(String idPackageProfile) {
        return ResponseEntity.ok(packageManagerService.getDetailPackageProfile(idPackageProfile));
    }

    @Override
    public ResponseEntity<Object> create(MultipartFile images, @Valid PackageProfileDTO data) {
        return ResponseEntity.ok(packageManagerService.createPackageProfile(data, images));
    }

    @Override
    public ResponseEntity<Object> updatePackageProfile(String idPackageProfile,  PackageProfileDTO data,MultipartFile images){
        return ResponseEntity.ok(packageManagerService.updatePackageProfile(idPackageProfile, data, images));
    }

    @Override
    public ResponseEntity<Object> updateStatusPackageProfile(String idPackageProfile, int status) {
        return ResponseEntity.ok(packageManagerService.updateStatusPackageProfile(idPackageProfile, status));
    }

    @Override
    public ResponseEntity<Object> deletePackageProfile(String idPackageProfile) {
        packageManagerService.deletePackageProfile(idPackageProfile);
        return ResponseEntity.ok("Delete package profile success");
    }

    @Override
    public ResponseEntity<Object> freePackageProfile() {
        return ResponseEntity.ok(packageManagerService.getListPackageProfileFree());
    }
}
