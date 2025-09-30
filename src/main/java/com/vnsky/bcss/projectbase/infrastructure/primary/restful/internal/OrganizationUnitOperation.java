package com.vnsky.bcss.projectbase.infrastructure.primary.restful.internal;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.CheckOrgParentRequest;
import com.vnsky.bcss.projectbase.domain.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("${application.path.base.private}/organization-unit")
public interface OrganizationUnitOperation {
    @PostMapping("/check-org-parent")
    @Operation(summary = "Kiểm tra đại lý cha")
    ResponseEntity<Object> checkOrgParent(@RequestBody CheckOrgParentRequest request);


    @GetMapping("/get-user-child/{parentId}")
    ResponseEntity<List<OrganizationUnitDTO>> getOrganizationUserChild(@PathVariable("parentId") String parentId);

    @PostMapping("/get-org-name/{userId}/{clientId}/{currentClientId}")
    ResponseEntity<List<UserDTO>> getOrgName(@PathVariable("userId") String userId,
                                             @PathVariable("clientId") String clientId,
                                             @PathVariable("currentClientId") String currentClientId,
                                             @RequestBody List<UserDTO> users);

    @GetMapping("/get-info-org-unit/{ordId}")
    ResponseEntity<OrganizationUnitDTO> getOrganizationUnit(@PathVariable("ordId") String ordId);
}
