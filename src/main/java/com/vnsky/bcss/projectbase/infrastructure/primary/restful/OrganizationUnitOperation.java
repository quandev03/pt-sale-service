package com.vnsky.bcss.projectbase.infrastructure.primary.restful;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.CheckOrgParentRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.GetAllOrganizationUnitResponse;
import com.vnsky.kafka.annotation.AuditAction;
import com.vnsky.kafka.annotation.AuditDetail;
import com.vnsky.kafka.annotation.AuditId;
import com.vnsky.kafka.constant.AuditActionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@RequestMapping("${application.path.base.public}/organization-unit")
public interface OrganizationUnitOperation {
    @GetMapping("/stores")
    List<GetAllOrganizationUnitResponse> getAllStores(@RequestParam(required = false) Boolean isAll);

    @GetMapping("/available")
    @Operation(summary = "Lấy danh sách phòng chưa thuê (Public - không cần xác thực)")
    @ApiResponse(responseCode = "200", description = "Danh sách phòng chưa thuê")
    ResponseEntity<List<com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationUnitResponse>> getAvailableRooms();

    @PostMapping
    @Operation(summary = "Add Organization Unit")
    @ApiResponse(responseCode = "200")
    @AuditAction(targetType = "SERIAL_STOCK", actionType = AuditActionType.CREATE)
    ResponseEntity<Object> addOrganizationUnit(
        @Valid
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Sample request add organization unit",
            required = true,
            content = @Content(
                schema = @Schema(implementation = OrganizationUnitDTO.class),
                examples = {
                    @ExampleObject(
                        name = "This is sample response send add organization unit",
                        summary = "Sample Request",
                        value = "{" +
                            "    \"parentId\": 1," +
                            "    \"orgCode\": \"ORG_4\"," +
                            "    \"orgName\": \"Organization Unit 1\"," +
                            "    \"provinceCode\": \"01\"," +
                            "    \"districtCode\": \"019\"," +
                            "    \"wardCode\": \"019\"," +
                            "    \"address\": \"8C Tôn Thất Thuyết\"," +
                            "    \"taxCode\": \"123123\"," +
                            "    \"representative\": \"REPRESENTATIVE 1\"," +
                            "    \"orgSubType\": \"01\"," +
                            "    \"status\": 1" +
                            "}"
                    )
                }
            )
        )
        @RequestBody OrganizationUnitDTO organizationUnitDTO);

    /**
     * API cập nhật đơn vị
     *
     * @param organizationUnitDTO Add Organization Unit
     * @return responseEntity
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update Organization Unit")
    @AuditAction(targetType = "SERIAL_STOCK", actionType = AuditActionType.UPDATE)
    @ApiResponse(
        responseCode = "200",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = {
                @ExampleObject(
                    name = "This is sample response send update organization unit",
                    summary = "Sample Response",
                    value = "{ " +
                        "        \"createdBy\": \"admin\", " +
                        "        \"createdDate\": null, " +
                        "        \"modifiedBy\": \"admin\", " +
                        "        \"modifiedDate\": null, " +
                        "        \"id\": 33, " +
                        "        \"parentId\": null, " +
                        "        \"orgCode\": \"ORG_7\", " +
                        "        \"orgName\": \"Organization Unit 2\", " +
                        "        \"orgType\": \"NBO\", " +
                        "        \"orgSubType\": \"01\", " +
                        "        \"orgDescription\": null, " +
                        "        \"provinceCode\": \"01\", " +
                        "        \"districtCode\": \"019\", " +
                        "        \"address\": \"8C Tôn Thất Thuyết\", " +
                        "        \"status\": true, " +
                        "        \"taxCode\": \"123123\", " +
                        "        \"contractNo\": null, " +
                        "        \"contractDate\": null, " +
                        "        \"representative\": \"REPRESENTATIVE 1\" " +
                        "    }"
                )
            }
        )
    )
    ResponseEntity<Object> updateOrganizationUnit(
        @Valid
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Sample request update organization unit",
            required = true,
            content = @Content(
                schema = @Schema(implementation = OrganizationUnitDTO.class),
                examples = {
                    @ExampleObject(
                        name = "This is sample response send update organization unit",
                        summary = "Sample Request",
                        value = "{ " +
                            "    \"parentId\": \"ORG_7\", " +
                            "    \"orgCode\": \"ORG_7\", " +
                            "    \"orgName\": \"Organization Unit 2\", " +
                            "    \"provinceCode\": \"01\", " +
                            "    \"districtCode\": \"019\", " +
                            "    \"wardCode\": \"019\", " +
                            "    \"address\": \"8C Tôn Thất Thuyết\", " +
                            "    \"taxCode\": \"123123\", " +
                            "    \"representative\": \"REPRESENTATIVE 1\", " +
                            "    \"orgSubType\": \"01\", " +
                            "    \"status\": 1 " +
                            "}"
                    )
                }
            )
        )
        @RequestBody OrganizationUnitDTO organizationUnitDTO,
        @PathVariable @AuditId String id);


    /**
     * API lấy tất cả đơn vị
     *
     * @return responseEntity
     */
    @GetMapping("/find/partners-without-organization-limit")
    @Operation(summary = "Get All Organization Unit with org_type = Partner and without Organization limit")
    @ApiResponse(
        responseCode = "200",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = {
                @ExampleObject(
                    name = "This is sample response send get all organization unit",
                    summary = "Sample Response",
                    value = """
                        [
                            {
                                "id": 126,
                                "parentId": 1,
                                "orgCode": "partner",
                                "orgName": "thuypt",
                                "createdBy": "catalog",
                                "createdDate": "2024-09-13T16:51:27.000Z",
                                "modifiedBy": "catalog",
                                "modifiedDate": "2024-09-13T16:51:27.000Z",
                                "status": 1
                            }
                        ]
                        """
                )
            }
        )
    )
    ResponseEntity<Object> getAllPartnersWithoutOrganizationLimit();

    /**
     * API lấy đơn vị
     *
     * @return responseEntity
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Organization Unit")
    @AuditDetail(targetType = "SERIAL_STOCK")
    @ApiResponse(
        responseCode = "200",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = {
                @ExampleObject(
                    name = "This is sample response send get organization unit",
                    summary = "Sample Response",
                    value = """
                        {
                                "createdBy": "admin",
                                "createdDate": "2024-08-27T23:55:05",
                                "modifiedBy": "admin",
                                "modifiedDate": "2024-08-29T00:25:27",
                                "id": 33,
                                "parentId": 32,
                                "orgCode": "ORG_7",
                                "orgName": "Organization Unit 2",
                                "orgType": "NBO",
                                "orgSubType": "01",
                                "orgDescription": null,
                                "provinceCode": "01",
                                "districtCode": "019",
                                "address": "8C Tôn Thất Thuyết",
                                "status": true,
                                "taxCode": "123123",
                                "contractNo": null,
                                "contractDate": null,
                                "representative": "REPRESENTATIVE 1"
                            }
                        """
                )
            }
        )
    )
    ResponseEntity<Object> getOrganizationUnit(@PathVariable("id") @AuditId String id);

    /**
     * API lấy đơn vị
     *
     * @return responseEntity
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Organization Unit")
    @AuditAction(targetType = "SERIAL_STOCK", actionType = AuditActionType.DELETE)
    @ApiResponse(
        responseCode = "200"
    )
    ResponseEntity<Object> deleteOrganizationUnit(@PathVariable @AuditId String id);

    /**
     * API lấy tất cả đơn vị
     *
     * @return responseEntity
     */
    @GetMapping
    @Operation(summary = "Get All Organization Unit")
    @ApiResponse(
        responseCode = "200",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = {
                @ExampleObject(
                    name = "This is sample response send get all organization unit",
                    summary = "Sample Response",
                    value = "{" +
                        "        \"createdBy\": \"admin\"," +
                        "        \"createdDate\": \"2024-08-27T23:08:44.1326392\"," +
                        "        \"modifiedBy\": \"admin\"," +
                        "        \"modifiedDate\": \"2024-08-27T23:08:44.1326392\"," +
                        "        \"id\": 30," +
                        "        \"parentId\": null," +
                        "        \"orgCode\": \"ORG_4\"," +
                        "        \"orgName\": \"Organization Unit 1\"," +
                        "        \"orgType\": \"NBO\"," +
                        "        \"orgSubType\": \"01\"," +
                        "        \"orgDescription\": null," +
                        "        \"provinceCode\": \"01\"," +
                        "        \"districtCode\": \"019\"," +
                        "        \"address\": \"8C Tôn Thất Thuyết\"," +
                        "        \"status\": true," +
                        "        \"taxCode\": \"123123\"," +
                        "        \"contractNo\": null," +
                        "        \"contractDate\": null," +
                        "        \"representative\": \"REPRESENTATIVE 1\"" +
                        "    }"
                )
            }
        )
    )
    ResponseEntity<Object> getAllOrganizationUnit(@RequestParam(value = "status", required = false) Integer status,
                                                  @RequestParam(value = "org-type", required = false) String orgType,
                                                  @RequestParam(value = "org-sub-type", required = false) String orgSubType,
                                                  @RequestParam(value = "textSearch", required= false) String textSearch,
                                                  @RequestParam(value = "rentalStatus", required = false) String rentalStatus);


    @GetMapping("/get-authorized-by-org-type")
    @Operation(summary = "Api lấy danh sách kho được phân quyền theo loại kho")
    ResponseEntity<Object> getAuthorizedOrganizationUnitByType(@RequestParam(required = false) String orgSubType);

    @PostMapping("/check-org-parent")
    @Operation(summary = "Kiểm tra đại lý cha")
    ResponseEntity<Object> checkOrgParent(@RequestBody CheckOrgParentRequest request);


    @GetMapping("/get-user-child/{parentId}")
    ResponseEntity<List<OrganizationUnitDTO>> getOrganizationUserChild(@PathVariable("parentId") String parentId);

    @GetMapping("/get-user-child")
    ResponseEntity<List<OrganizationUnitDTO>> getOrganizationUserChildCurrent();

    @GetMapping("/get-limit")
    ResponseEntity<Object> getDebitLimit();

    @PostMapping("/{id}/images")
    @Operation(summary = "Upload images for organization unit")
    ResponseEntity<Object> uploadImages(
        @PathVariable("id") String orgUnitId,
        @RequestParam("files") List<MultipartFile> files);

    @GetMapping("/{id}/images")
    @Operation(summary = "Get image URLs for organization unit")
    ResponseEntity<Object> getImageUrls(@PathVariable("id") String orgUnitId);

    @GetMapping("/{id}/images/{imageId}")
    @Operation(summary = "Download image for organization unit")
    ResponseEntity<Resource> downloadImage(
        @PathVariable("id") String orgUnitId,
        @PathVariable("imageId") String imageId);

    @PutMapping("/{id}/images")
    @Operation(summary = "Update images for organization unit (delete old and upload new)")
    ResponseEntity<Object> updateImages(
        @PathVariable("id") String orgUnitId,
        @RequestParam("files") List<MultipartFile> files);
}
