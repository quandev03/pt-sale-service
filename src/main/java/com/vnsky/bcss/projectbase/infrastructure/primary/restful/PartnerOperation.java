package com.vnsky.bcss.projectbase.infrastructure.primary.restful;

import com.vnsky.bcss.projectbase.domain.dto.PartnerRegistrationRequest;
import com.vnsky.bcss.projectbase.domain.dto.PartnerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Partner Operation", description = "API quản lý đối tác")
@RequestMapping("${application.path.base.private}/partners")
public interface PartnerOperation {

    @PostMapping
    @Operation(summary = "Đăng ký đối tác (tạo kèm admin nếu truyền)")
    ResponseEntity<PartnerResponse> registerPartner(@RequestBody PartnerRegistrationRequest request);

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết đối tác")
    ResponseEntity<PartnerResponse> getPartner(@PathVariable Long id);

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin đối tác")
    ResponseEntity<PartnerResponse> updatePartner(@PathVariable Long id, @RequestBody PartnerRegistrationRequest request);
}


