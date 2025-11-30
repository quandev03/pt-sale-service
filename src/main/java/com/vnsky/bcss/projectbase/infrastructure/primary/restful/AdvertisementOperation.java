package com.vnsky.bcss.projectbase.infrastructure.primary.restful;

import com.vnsky.bcss.projectbase.infrastructure.data.request.CreateAdvertisementRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.UpdateAdvertisementRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.AdvertisementResponse;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.AdvertisementStatus;
import com.vnsky.kafka.annotation.AuditAction;
import com.vnsky.kafka.annotation.AuditId;
import com.vnsky.kafka.constant.AuditActionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Advertisement Operation", description = "API quản lý quảng cáo")
@RequestMapping("${application.path.base.public}/advertisements")
public interface AdvertisementOperation {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Tạo quảng cáo", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Quảng cáo đã được tạo")
    @AuditAction(targetType = "ADVERTISEMENT", actionType = AuditActionType.CREATE)
    ResponseEntity<AdvertisementResponse> create(
        @RequestPart("request") @Valid CreateAdvertisementRequest request,
        @RequestPart(value = "image", required = false) MultipartFile image) throws IOException;

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Cập nhật quảng cáo", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Quảng cáo đã được cập nhật")
    @AuditAction(targetType = "ADVERTISEMENT", actionType = AuditActionType.UPDATE)
    ResponseEntity<AdvertisementResponse> update(
        @PathVariable @AuditId String id,
        @RequestPart("request") @Valid UpdateAdvertisementRequest request,
        @RequestPart(value = "image", required = false) MultipartFile image);

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa quảng cáo", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Quảng cáo đã được xóa")
    @AuditAction(targetType = "ADVERTISEMENT", actionType = AuditActionType.DELETE)
    ResponseEntity<Object> delete(@PathVariable @AuditId String id);

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết quảng cáo", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200")
    ResponseEntity<AdvertisementResponse> getById(@PathVariable String id);

    @GetMapping
    @Operation(summary = "Lấy danh sách quảng cáo", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200")
    ResponseEntity<List<AdvertisementResponse>> getAll(
        @RequestParam(required = false) AdvertisementStatus status);

    @GetMapping("/active")
    @Operation(summary = "Lấy danh sách quảng cáo đang diễn ra (Public - không cần xác thực)")
    @ApiResponse(responseCode = "200", description = "Danh sách quảng cáo đang diễn ra")
    ResponseEntity<List<AdvertisementResponse>> getActiveAdvertisements();

    @GetMapping("/random")
    @Operation(summary = "Lấy quảng cáo ngẫu nhiên đang diễn ra (Public - không cần xác thực)")
    @ApiResponse(responseCode = "200", description = "Quảng cáo ngẫu nhiên đang diễn ra")
    ResponseEntity<AdvertisementResponse> getRandomActiveAdvertisement();

    @PostMapping("/{id}/increment-view")
    @Operation(summary = "Tăng lượt xem quảng cáo (Public - không cần xác thực)")
    @ApiResponse(responseCode = "200", description = "Lượt xem đã được tăng")
    ResponseEntity<Object> incrementViewCount(@PathVariable String id);
}

