package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner;

import com.vnsky.bcss.projectbase.domain.dto.AgentDebitDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.AgentDebitRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.partner.TotalAmountResponse;
import com.vnsky.kafka.annotation.AuditAction;
import com.vnsky.kafka.constant.AuditActionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequestMapping("${application.path.base.public}/agent-debit")
@Tag(name = "Agent Debit", description = "API quản lý công nợ của đại lý")
public interface AgentDebitOperation {
    @PostMapping
    @Operation(
        summary = "Thêm công nợ đại lý",
        description = "Tạo một bản ghi công nợ mới cho đại lý dựa trên payment ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Tạo công nợ đại lý thành công",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dữ liệu đầu vào không hợp lệ",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Lỗi hệ thống",
            content = @Content(mediaType = "application/json")
        )
    })
    @AuditAction(targetType = "AGENT_DEBIT", actionType = AuditActionType.CREATE)
    ResponseEntity<Object> addAgentDebit(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Thông tin tạo công nợ",
            required = true,
            content = @Content(
                schema = @Schema(implementation = AgentDebitRequest.class),
                examples = {
                    @ExampleObject(
                        name = "Ví dụ",
                        value = """
                        {
                          "voucherCode": "VCB23948051",
                          "voucherType": "2"
                        }
                        """
                    )
                }
            )
        )
        @RequestBody AgentDebitRequest data
        );

    @GetMapping
    @Operation(
        summary = "Lấy danh sách công nợ đại lý",
        description = "Truy vấn danh sách công nợ của các đại lý với các điều kiện lọc"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lấy danh sách công nợ thành công",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Page.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Tham số truy vấn không hợp lệ",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Lỗi hệ thống",
            content = @Content(mediaType = "application/json")
        )
    })
    ResponseEntity<Page<AgentDebitDTO>> getAgentDebit(
        @Parameter(description = "Từ khóa tìm kiếm (tìm theo mã giao dịch)", example = "PAY123456")
        @RequestParam(value = "q", required = false) String q,

        @Parameter(description = "Loại chứng từ: 1-Phiếu thu tiền mặt | 2-Sổ phụ ngân hàng", example = "2")
        @RequestParam(value = "type", required = false) String type,

        @Parameter(description = "Ngày bắt đầu để lọc (YYYY-MM-DD)", example = "2023-01-01")
        @RequestParam(value = "startDate", required = false) LocalDate startDate,

        @Parameter(description = "Ngày kết thúc để lọc (YYYY-MM-DD)", example = "2023-12-31")
        @RequestParam(value = "endDate", required = false) LocalDate endDate,

        @Parameter(description = "Thông tin phân trang (page, size, sort)")
        @PageableDefault Pageable pageable
    );

    @PostMapping("/export")
    @Operation(
        summary = "Xuất excel giao dịch nạp tiền BHTT",
        description = "Xuất danh sách giao dịch nạp tiền BHTT ra file Excel. Tên file theo format: BHTT-ddmmyyyyhhmmss.xlsx"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Xuất file Excel thành công",
            content = @Content(
                mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                schema = @Schema(type = "string", format = "binary")
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Tham số ngày tháng không hợp lệ",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Lỗi hệ thống khi xuất file",
            content = @Content(mediaType = "application/json")
        )
    })
    ResponseEntity<Object> export(
        @Parameter(description = "Từ khóa tìm kiếm (tìm theo mã giao dịch)", example = "PAY123456")
        @RequestParam(value = "q", required = false) String q,

        @Parameter(description = "Loại chứng từ: 1-Phiếu thu tiền mặt | 2-Sổ phụ ngân hàng", example = "2")
        @RequestParam(value = "type", required = false) String type,

        @Parameter(description = "Ngày bắt đầu để lọc (YYYY-MM-DD)", example = "2023-01-01")
        @RequestParam(value = "startDate", required = false) LocalDate startDate,

        @Parameter(description = "Ngày kết thúc để lọc (YYYY-MM-DD)", example = "2023-12-31")
        @RequestParam(value = "endDate", required = false) LocalDate endDate,

        @Parameter(description = "Thông tin phân trang (page, size, sort)")
        @PageableDefault Pageable pageable
    );

    @GetMapping("/total-amount")
    @Operation(
        summary = "Lấy tổng số tiền nạp BHTT",
        description = "API này trả về tổng số tiền đã nạp BHTT trong khoảng thời gian (nếu có lọc theo ngày)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Truy vấn thành công",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TotalAmountResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Tham số ngày tháng không hợp lệ",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Lỗi hệ thống",
            content = @Content(mediaType = "application/json")
        )
    })
    ResponseEntity<Object> getTotalAmount();

}
