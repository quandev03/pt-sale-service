package com.vnsky.bcss.projectbase.infrastructure.primary.restful.impl;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUserDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.OrganizationUserServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.CreateOrganizationUserRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationCurrentResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationDTOResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.UserInfoResponse;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.OrganizationUserPrivateOperation;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.kafka.annotation.AuditAction;
import com.vnsky.kafka.annotation.AuditDetail;
import com.vnsky.kafka.annotation.AuditId;
import com.vnsky.kafka.constant.AuditActionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
public class OrganizationUserPrivateRest implements OrganizationUserPrivateOperation {
    private final OrganizationUserServicePort organizationUserServicePort;

    @Override
    public ResponseEntity<Object> save(OrganizationUserDTO request) {
        return ResponseEntity.ok(organizationUserServicePort.savePrivate(request));
    }
}
