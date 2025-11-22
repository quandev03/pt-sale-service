package com.vnsky.bcss.projectbase.infrastructure.primary.restful;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUserDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Organization User Operation", description = "API liên quan đến người dùng")
@RequestMapping("${application.path.base.private}/organization-user")
public interface OrganizationUserPrivateOperation {

    @PostMapping
    ResponseEntity<Object> save(@RequestBody OrganizationUserDTO request);

}
