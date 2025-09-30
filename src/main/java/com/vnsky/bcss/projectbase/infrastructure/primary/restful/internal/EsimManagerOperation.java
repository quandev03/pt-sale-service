package com.vnsky.bcss.projectbase.infrastructure.primary.restful.internal;


import com.vnsky.bcss.projectbase.domain.dto.ActionHistoryDTO;
import com.vnsky.bcss.projectbase.domain.dto.EsimInforDTO;
import com.vnsky.bcss.projectbase.domain.dto.PackageProfileDTO;
import com.vnsky.bcss.projectbase.domain.dto.SubscriberDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.SendQrCodeRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.ESimDetailResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Esim Manager we", description = "Manage package profile")
@RequestMapping("${application.path.base.private}/esim-manager")
public interface EsimManagerOperation {


    @Parameter(name = "size", example = "10")
    @Parameter(name = "page", example = "0")
    @GetMapping
    ResponseEntity<Page<EsimInforDTO>> getListEsimInforDTO(
        @RequestParam(name = "textSearch", required = false) String textSearch,
        @RequestParam(name = "subStatus", required = false) Integer subStatus,
        @RequestParam(name = "activeStatus", required = false) Integer activeStatus,
        @RequestParam(name = "pckCode", required = false)  String pckCode,
        @RequestParam(name = "orgId", required = false) List<String> orgId,
        @RequestParam(name = "fromDate", required = false)  String fromDate,
        @RequestParam(name = "toDate", required = false) String toDate,
        Pageable pageable
    );

    @GetMapping("/{subId}")
    ResponseEntity<List<ActionHistoryDTO>> getActionHistoryDTO(@PathVariable String subId);


    @GetMapping("/detail/{subId}")
    ResponseEntity<ESimDetailResponse> getEsimInforDTO(@PathVariable String subId);

    @GetMapping("/package")
    ResponseEntity<List<PackageProfileDTO>> getPackageProfile();

    @GetMapping("/organization-unit")
    ResponseEntity<Object> getOrganizationUnit();

    @GetMapping("/export")
    ResponseEntity<Resource> export(
        @RequestParam(name = "textSearch", required = false) String textSearch,
        @RequestParam(name = "subStatus", required = false) Integer subStatus,
        @RequestParam(name = "activeStatus", required = false) Integer activeStatus,
        @RequestParam(name = "pckCode", required = false)  String pckCode,
        @RequestParam(name = "orgId", required = false)  List<String> orgId,
        @RequestParam(name = "fromDate", required = false) String fromDate,
        @RequestParam(name = "toDate",  required = false) String toDate
    );

}
