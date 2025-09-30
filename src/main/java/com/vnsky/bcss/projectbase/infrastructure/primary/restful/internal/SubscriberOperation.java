package com.vnsky.bcss.projectbase.infrastructure.primary.restful.internal;

import com.vnsky.bcss.projectbase.infrastructure.data.response.SubscriberStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Subscriber internal apis")
@RequestMapping("${application.path.base.private}/subscriber")
public interface SubscriberOperation {
    @GetMapping("/{isdn}")
    ResponseEntity<Object> findByIsdn(@PathVariable Long isdn);

    @GetMapping("/file")
    ResponseEntity<Object> downloadFile( @RequestParam String file);

    @GetMapping
    @Operation(summary = "danh sách số")
    @ApiResponse(
        responseCode = "200"
    )
    ResponseEntity<Object> search(@RequestParam(required = false) String q,
                                  @RequestParam(required = false) Integer status,
                                  @RequestParam(required = false) String orgCode,
                                  @PageableDefault Pageable pageable);

    @PostMapping("/export-excel")
    @Operation(summary = "xuất excel danh sách số")
    @ApiResponse(
        responseCode = "200"
    )
    ResponseEntity<Object> exportExcel(@RequestParam(required = false) String q,
                                  @RequestParam(required = false) Integer status,
                                  @RequestParam(required = false) String orgCode);

    @GetMapping("/status")
    ResponseEntity<List<SubscriberStatusResponse>> getAllStatus();
}
