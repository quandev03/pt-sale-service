package com.vnsky.bcss.projectbase.infrastructure.primary.restful;


import com.vnsky.bcss.projectbase.domain.dto.OrganizationUserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Partner Users", description = "API quản lý người dùng thuộc đối tác")
@RequestMapping("${application.path.base.private}/partner-users")
public interface OrganizationUserOperation {
    @PostMapping
    @Operation(summary = "Tạo người dùng thuộc đối tác")
    ResponseEntity<OrganizationUserDTO> addPartnerUser(@RequestBody OrganizationUserDTO organizationUnitDTO);

    @PutMapping("/unit/{userId}")
    @Operation(summary = "Cập nhật đối tác của người dùng")
    ResponseEntity<Object> updatePartnerOfUser(
        @Parameter(description = "ID người dùng") @PathVariable(name = "userId") String userId,
        @Parameter(description = "ID đối tác") @RequestParam("partnerId") String partnerId
    );

    ResponseEntity<OrganizationUserDTO> addOrganizationUnit(OrganizationUserDTO request);

    ResponseEntity<Object> updateOrganizationUnit(String userId, String orgId);
}
