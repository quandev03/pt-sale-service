package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner;


import com.vnsky.bcss.projectbase.domain.dto.ActionHistoryDTO;
import com.vnsky.bcss.projectbase.domain.dto.EsimInforDTO;
import com.vnsky.bcss.projectbase.domain.dto.PackageProfileDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.ExportQrCodeRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.SendQrCodeRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationUnitResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.ESimDetailResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Esim Manager we", description = "Manage package profile")
@RequestMapping("${application.path.base.public}/esim-manager")
public interface EsimManagerOperation {


    @Parameter(name = "size", example = "10")
    @Parameter(name = "page", example = "0")
    @GetMapping
    ResponseEntity<Page<EsimInforDTO>> getListEsimInforDTO(
        @RequestParam(name = "textSearch", required = false) String textSearch,
        @RequestParam(name = "subStatus", required = false) Integer subStatus,
        @RequestParam(name = "activeStatus", required = false) Integer activeStatus,
        @RequestParam(name = "pckCode", required = false)  String pckCode,
        @RequestParam(name = "orgId", required = false)  List<String> orgId,
        @RequestParam(name = "fromDate" ,required = false)  String fromDate,
        @RequestParam(name = "toDate", required = false)  String toDate,
        Pageable pageable
    );

    @GetMapping("/{subId}")
    ResponseEntity<List<ActionHistoryDTO>> getActionHistoryDTO(@PathVariable String subId);

    @GetMapping("/detail/{subId}")
    ResponseEntity<ESimDetailResponse> getEsimInforDTO(@PathVariable String subId);

    @PostMapping("/sent-qr-code")
    ResponseEntity<Object> sentQrcode(@RequestBody SendQrCodeRequest request);

    @PostMapping("/gen-qr-code/{subId}")
    ResponseEntity<Resource> sentQrcode(@PathVariable String subId, @RequestParam(value = "size", required = false) String size);

    @GetMapping("/package")
    ResponseEntity<List<PackageProfileDTO>> getPackageProfile();

    @GetMapping("/esim-qr")
    ResponseEntity<Resource> esimGenerateQR(@RequestParam("data") String data, @RequestParam("size")String size);

    @GetMapping("/organization-unit")
    ResponseEntity<List<OrganizationUnitResponse>> getOrganizationUnit();

    @PostMapping("/export")
    ResponseEntity<Resource> export(
        @RequestParam(name = "textSearch", required = false) String textSearch,
        @RequestParam(name = "subStatus", required = false) Integer subStatus,
        @RequestParam(name = "activeStatus", required = false) Integer activeStatus,
        @RequestParam(name = "pckCode", required = false)  String pckCode,
        @RequestParam(name = "orgId", required = false)  List<String> orgId,
        @RequestParam(name = "fromDate", required = false) String fromDate,
        @RequestParam(name = "toDate", required = false) String toDate
    );

    @PostMapping("/export-qr")
    ResponseEntity<Resource> exportQrCode(@RequestBody ExportQrCodeRequest request);
}
