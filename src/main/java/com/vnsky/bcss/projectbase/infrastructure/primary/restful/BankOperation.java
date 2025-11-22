package com.vnsky.bcss.projectbase.infrastructure.primary.restful;

import com.vnsky.bcss.projectbase.infrastructure.data.response.BankResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Bank Operation", description = "API liên quan đến ngân hàng")
@RequestMapping("${application.path.base.public}/banks")
public interface BankOperation {

    @GetMapping
    @Operation(summary = "Lấy danh sách ngân hàng từ VietQR")
    @ApiResponse(responseCode = "200", description = "Danh sách ngân hàng")
    ResponseEntity<List<BankResponse>> getAllBanks();
}

