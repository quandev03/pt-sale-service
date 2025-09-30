package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner;

import com.vnsky.bcss.projectbase.domain.dto.SalePackageDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Sale package Operation", description = "API bán gói cước cho thuê bao")
@RequestMapping("${application.path.base.public}/sale-package")
public interface SalePackageOperation {

    @Operation(summary = "Api kiểm tra số thuê bao + lấy danh sách gói cước",
        description = """
            {
                     "packageId": "1a4af3f1-696d-4474-b84b-b55ee010a2dc",
                     "packageCode": "SKY5",
                     "cycle": 1,
                     "unit": "day"
                 },
                 {
                     "packageId": "61e42db1-5961-4460-8457-3855cadc9ff8",
                     "packageCode": "7SKY5",
                     "cycle": 7,
                     "unit": "day"
                 },
            """)
    @GetMapping("/check-isdn")
    ResponseEntity<Object> checkIsdn(@RequestParam String isdn, @RequestParam(value = "type", required = false, defaultValue = "1") Integer type);

    @Operation(summary = "api register package",
        description = """

            """)
    @PostMapping("/register-package")
    ResponseEntity<Object> registerPackage(@Valid @RequestBody SalePackageDTO salePackage);

    @Operation(summary = "check thong tin file theo lo")
    @PostMapping("/check-data")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
    ResponseEntity<Object> checkData(@RequestPart(name = "attachment") MultipartFile attachment);

    @Operation(summary = "submit thong tin file theo lo")
    @PostMapping("/submit-data")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
    ResponseEntity<Object> submitData(@RequestPart(name = "attachment") MultipartFile attachment);

    @GetMapping(value = "/action/get-excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    ResponseEntity<Object> getSampleCreateTicketOut();

    @Operation(summary = "Search batch package sales with pagination and filtering",
        description = "Search for batch package sales based on various criteria with pagination support")
    @GetMapping("/batch-sales")
    @Parameter(name = "size", example = "20")
    @Parameter(name = "page", example = "0")
    ResponseEntity<Object> searchBatchPackageSales(
        @RequestParam(name = "q", required = false) String q,
        @RequestParam(name = "saleType", required = false) Integer saleType,
        @RequestParam(name = "status", required = false) Integer status,
        @RequestParam(name = "fromDate", required = false) String fromDate,
        @RequestParam(name = "toDate", required = false) String toDate,
        @Parameter(hidden = true) @PageableDefault Pageable pageable
    );
}
