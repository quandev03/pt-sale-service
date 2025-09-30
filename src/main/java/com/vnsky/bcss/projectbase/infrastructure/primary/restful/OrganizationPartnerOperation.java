package com.vnsky.bcss.projectbase.infrastructure.primary.restful;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.PackageClientRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.GetAllOrganizationUnitResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Organization Partner Operation", description = "API liên quan đến danh mục đối tác")
@RequestMapping("${application.path.base.private}/organization-partner")
public interface OrganizationPartnerOperation {

    /**
     * API tạo đối tác
     *
     * @return responseEntity
     * @throws com.vnsky.common.exception.domain.BaseException BaseException
     */
    @PostMapping
    @Operation(summary = "Tạo đối tác")
    @ApiResponse(
        responseCode = "200"
    )
    ResponseEntity<Object> createPartner(@RequestPart @Validated OrganizationUnitDTO organizationUnitDTO,
                                         @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, required = false) String acceptLanguage);

    /**
     * API cập nhật đối tác
     *
     * @return responseEntity
     * @throws com.vnsky.common.exception.domain.BaseException BaseException
     */
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật đối tác")
    @ApiResponse(
        responseCode = "200"
    )
    ResponseEntity<Object> updatePartner(@PathVariable String id,
                                         @RequestPart OrganizationUnitDTO organizationUnitDTO);

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết đối tác")
    @ApiResponse(
        responseCode = "200"
    )
    ResponseEntity<Object> detailPartner(@PathVariable String id);

    @GetMapping
    @Operation(summary = "danh sách đối tác")
    @ApiResponse(
        responseCode = "200"
    )
    ResponseEntity<Object> search(@RequestParam(required = false) String q,
                                  @RequestParam(required = false) String partnerType,
                                  @RequestParam(required = false) Integer status,
                                  @RequestParam(required = false) Integer approvalStatus,
                                  @PageableDefault Pageable pageable);


    @PutMapping("/{id}/update-status")
    @Operation(summary = "Cập nhật trạng thái đối tác")
    @ApiResponse(
        responseCode = "200"
    )
    ResponseEntity<Void> updateStatusPartner(@PathVariable String id, @RequestParam Integer status);

    @GetMapping("/get-unit-by-code/{code}")
    ResponseEntity<List<GetAllOrganizationUnitResponse>> getUnitByCode(@PathVariable String code);

    @GetMapping("/all-partner")
    ResponseEntity<List<GetAllOrganizationUnitResponse>> getAllPartner(@RequestParam(required = false) String q);

    @PostMapping("/delivery/info")
    ResponseEntity<Object> getDeliveryInfo(@RequestPart(required = false) MultipartFile cardFront,
                                           @RequestPart(required = false) MultipartFile cardBack,
                                           @RequestPart(required = false) MultipartFile portrait);

    @GetMapping("/get-by-code/{code}")
    ResponseEntity<Object> getByCode(@PathVariable String code);

    @PostMapping("/packages")
    @Operation(summary = "Phân quyền gói cước")
    @ApiResponse(
        responseCode = "200"
    )
    ResponseEntity<Void> createPackageClientForClient(@RequestBody @Validated PackageClientRequest packageClientRequest);

    @GetMapping("/{clientId}/packages")
    ResponseEntity<Object> getAllPackageByClient(@PathVariable String clientId);

    @GetMapping("/get-org-nbo/{orgPartnerID}")
    ResponseEntity<Object> getOrgNBO(@PathVariable String orgPartnerID);
}
