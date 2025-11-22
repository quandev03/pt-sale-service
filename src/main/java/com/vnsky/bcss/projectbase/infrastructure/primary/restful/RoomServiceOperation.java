package com.vnsky.bcss.projectbase.infrastructure.primary.restful;

import com.vnsky.bcss.projectbase.infrastructure.data.request.CreateRoomServiceRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.UpdateRoomServiceRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.RoomServiceResponse;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomServiceType;
import com.vnsky.kafka.annotation.AuditAction;
import com.vnsky.kafka.annotation.AuditId;
import com.vnsky.kafka.constant.AuditActionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("${application.path.base.public}/room-services")
public interface RoomServiceOperation {

    @PostMapping
    @Operation(summary = "Tạo dịch vụ phòng")
    @ApiResponse(
        responseCode = "200",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = {
                @ExampleObject(
                    name = "Sample Response",
                    summary = "Sample Response",
                    value = """
                        {
                            "id": "123e4567-e89b-12d3-a456-426614174000",
                            "orgUnitId": "org-unit-id",
                            "serviceType": "ELECTRICITY",
                            "serviceCode": "ELECTRICITY",
                            "serviceName": "Điện",
                            "price": 500000.00,
                            "clientId": "client-id",
                            "status": 1,
                            "createdBy": "admin",
                            "createdDate": "2024-11-23T10:00:00",
                            "modifiedBy": null,
                            "modifiedDate": null
                        }
                        """
                )
            }
        )
    )
    @AuditAction(targetType = "ROOM_SERVICE", actionType = AuditActionType.CREATE)
    ResponseEntity<RoomServiceResponse> createRoomService(
        @Valid @RequestBody CreateRoomServiceRequest request);

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật dịch vụ phòng")
    @ApiResponse(
        responseCode = "200",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = {
                @ExampleObject(
                    name = "Sample Response",
                    summary = "Sample Response",
                    value = """
                        {
                            "id": "123e4567-e89b-12d3-a456-426614174000",
                            "orgUnitId": "org-unit-id",
                            "serviceType": "ELECTRICITY",
                            "serviceCode": "ELECTRICITY",
                            "serviceName": "Điện",
                            "price": 600000.00,
                            "clientId": "client-id",
                            "status": 1,
                            "createdBy": "admin",
                            "createdDate": "2024-11-23T10:00:00",
                            "modifiedBy": "admin",
                            "modifiedDate": "2024-11-23T11:00:00"
                        }
                        """
                )
            }
        )
    )
    @AuditAction(targetType = "ROOM_SERVICE", actionType = AuditActionType.UPDATE)
    ResponseEntity<RoomServiceResponse> updateRoomService(
        @PathVariable @AuditId String id,
        @Valid @RequestBody UpdateRoomServiceRequest request);

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa dịch vụ phòng")
    @ApiResponse(responseCode = "200")
    @AuditAction(targetType = "ROOM_SERVICE", actionType = AuditActionType.DELETE)
    ResponseEntity<Object> deleteRoomService(@PathVariable @AuditId String id);

    @GetMapping
    @Operation(summary = "Lấy danh sách dịch vụ phòng")
    @ApiResponse(
        responseCode = "200",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = {
                @ExampleObject(
                    name = "Sample Response",
                    summary = "Sample Response",
                    value = """
                        [
                            {
                                "id": "123e4567-e89b-12d3-a456-426614174000",
                                "orgUnitId": "org-unit-id",
                                "serviceType": "ELECTRICITY",
                                "serviceCode": "ELECTRICITY",
                                "serviceName": "Điện",
                                "price": 500000.00,
                                "clientId": "client-id",
                                "status": 1,
                                "createdBy": "admin",
                                "createdDate": "2024-11-23T10:00:00",
                                "modifiedBy": null,
                                "modifiedDate": null
                            }
                        ]
                        """
                )
            }
        )
    )
    ResponseEntity<List<RoomServiceResponse>> getAllRoomServices(
        @RequestParam(required = false) String orgUnitId,
        @RequestParam(required = false) RoomServiceType serviceType,
        @RequestParam(required = false) Integer status);

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết dịch vụ phòng")
    @ApiResponse(
        responseCode = "200",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = {
                @ExampleObject(
                    name = "Sample Response",
                    summary = "Sample Response",
                    value = """
                        {
                            "id": "123e4567-e89b-12d3-a456-426614174000",
                            "orgUnitId": "org-unit-id",
                            "serviceType": "ELECTRICITY",
                            "serviceCode": "ELECTRICITY",
                            "serviceName": "Điện",
                            "price": 500000.00,
                            "clientId": "client-id",
                            "status": 1,
                            "createdBy": "admin",
                            "createdDate": "2024-11-23T10:00:00",
                            "modifiedBy": null,
                            "modifiedDate": null
                        }
                        """
                )
            }
        )
    )
    ResponseEntity<RoomServiceResponse> getRoomServiceById(@PathVariable String id);
}

