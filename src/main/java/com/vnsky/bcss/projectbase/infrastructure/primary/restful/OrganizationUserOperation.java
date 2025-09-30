package com.vnsky.bcss.projectbase.infrastructure.primary.restful;


import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.domain.dto.OrganizationUserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("${application.path.base.private}/organization-user")
public interface OrganizationUserOperation {
    @PostMapping
    ResponseEntity<OrganizationUserDTO> addOrganizationUnit(@RequestBody OrganizationUserDTO organizationUnitDTO);

    @PutMapping("/unit/{userId}")
    ResponseEntity<Object> updateOrganizationUnit(
        @PathVariable(name = "userId") String userId,
        @RequestParam("orgId") String orgId
    );

}
