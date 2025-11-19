package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner;

import com.vnsky.bcss.projectbase.infrastructure.data.request.PartnerPackageSubscriptionCreateRequest;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Partner Package Subscription", description = "Quản lý gói dịch vụ đối tác đã mua")
@RequestMapping("${application.path.base.private}/partner-package-subscriptions")
public interface PartnerPackageSubscriptionOperation {

    @PostMapping
    ResponseEntity<Object> createSubscription(@RequestBody @Valid PartnerPackageSubscriptionCreateRequest request);

    @GetMapping
    ResponseEntity<Object> listSubscriptions(
        @RequestParam(required = false) String organizationUnitId,
        @RequestParam(required = false) String packageProfileId,
        @RequestParam(required = false) PartnerPackageSubscriptionStatus status,
        @PageableDefault Pageable pageable
    );

    @PostMapping("/{id}/stop")
    ResponseEntity<Object> stopSubscription(@PathVariable("id") String id);
}

