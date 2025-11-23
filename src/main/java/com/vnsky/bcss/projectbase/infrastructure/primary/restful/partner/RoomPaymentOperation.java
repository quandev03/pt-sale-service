package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner;

import com.vnsky.bcss.projectbase.infrastructure.data.response.RoomPaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Room Payment Operation", description = "API thanh toán dịch vụ phòng")
@RequestMapping("${application.path.base.public}/room-payments")
public interface RoomPaymentOperation {

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file Excel và tạo thanh toán dịch vụ phòng")
    @ApiResponse(
        responseCode = "200",
        description = "Danh sách thanh toán đã tạo"
    )
    ResponseEntity<List<RoomPaymentResponse>> uploadAndProcess(
        @RequestPart("file") MultipartFile file,
        @RequestPart(value = "month", required = true) String month,
        @RequestPart(value = "year", required = true) String year);

    @GetMapping
    @Operation(summary = "Lấy danh sách thanh toán")
    @ApiResponse(responseCode = "200")
    ResponseEntity<List<RoomPaymentResponse>> getAll(
        @RequestParam(required = false) String orgUnitId,
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month);

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết thanh toán")
    @ApiResponse(responseCode = "200")
    ResponseEntity<RoomPaymentResponse> getById(@PathVariable String id);

    @PostMapping("/{id}/resend-email")
    @Operation(summary = "Gửi lại email hóa đơn thanh toán")
    @ApiResponse(responseCode = "200", description = "Email đã được gửi lại thành công")
    ResponseEntity<Object> resendEmail(@PathVariable String id);

    @PostMapping("/{id}/generate-qr")
    @Operation(summary = "Tạo lại QR code thanh toán")
    @ApiResponse(responseCode = "200", description = "QR code URL đã được tạo")
    ResponseEntity<RoomPaymentResponse> generateQRCode(@PathVariable String id);
}

