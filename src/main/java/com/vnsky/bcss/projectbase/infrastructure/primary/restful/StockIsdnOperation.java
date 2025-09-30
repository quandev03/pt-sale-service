package com.vnsky.bcss.projectbase.infrastructure.primary.restful;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Stock_Isdn apis")
@RequestMapping("${application.path.base.private}/stock-isdn")
public interface StockIsdnOperation {
    @GetMapping
    @Operation(summary = "tra cứu số")
    @ApiResponse(
        responseCode = "200"
    )
    ResponseEntity<Object> search(@RequestParam(required = false) String q,
                                  @RequestParam(required = false) Integer status,
                                  @RequestParam(required = false) String orgCode,
                                  @PageableDefault Pageable pageable);

    @PostMapping("/export-excel")
    @Operation(summary = "xuất excel tra số")
    @ApiResponse(
        responseCode = "200"
    )
    ResponseEntity<Object> exportExcel(@RequestParam(required = false) String q,
                                       @RequestParam(required = false) Integer status,
                                       @RequestParam(required = false) String orgCode);
}
