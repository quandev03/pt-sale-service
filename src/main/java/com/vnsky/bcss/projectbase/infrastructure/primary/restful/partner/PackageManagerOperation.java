package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner;

import com.vnsky.bcss.projectbase.domain.dto.PackageProfileDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Package Manager", description = "Manage package profile")
@RequestMapping("${application.path.base.private}/package-manager")
public interface PackageManagerOperation {
    @GetMapping
    @Parameter(name = "size", example = "10")
    @Parameter(name = "page", example = "0")
    ResponseEntity<Object> getListPackage(
        @RequestParam(name = "pckCodeOrPckName", required = false) String pckCodeOrPckName,
        @RequestParam(name = "status", required = false) Integer status,
        @RequestParam(name = "minPrice", required = false) Long minPrice,
        @RequestParam(name = "maxPrice", required = false) Long maxPrice,
        @Parameter(hidden = true) @PageableDefault Pageable pageable
    );

    @GetMapping("/{idPackageProfile}")
    ResponseEntity<Object> getDetailPackageProfile(@PathVariable("idPackageProfile") String idPackageProfile);

    @PostMapping
    @Operation(summary = "Add Package Profile")
    ResponseEntity<Object> create(@RequestPart(required = false) MultipartFile images, @RequestPart @Valid PackageProfileDTO data);


    @PutMapping("/{idPackageProfile}")
    ResponseEntity<Object> updatePackageProfile(@PathVariable("idPackageProfile") String idPackageProfile ,@RequestPart @Valid PackageProfileDTO data, @RequestPart(required = false) MultipartFile images);

    @PutMapping("/{idPackageProfile}/status")
    ResponseEntity<Object> updateStatusPackageProfile(@PathVariable("idPackageProfile") String idPackageProfile, @RequestParam("status") int status);

    @DeleteMapping("/{idPackageProfile}")
    ResponseEntity<Object> deletePackageProfile(@PathVariable("idPackageProfile") String idPackageProfile);

    @GetMapping("/free")
    ResponseEntity<Object> freePackageProfile();

}
