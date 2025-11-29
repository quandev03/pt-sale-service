package com.vnsky.bcss.projectbase.infrastructure.primary.restful;

import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionDTO;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("${application.path.base.public}/sale-package-partner")
public interface SalePackageToPartnerOperation {
    @PostMapping
    ResponseEntity<Object> create(@RequestBody PartnerPackageSubscriptionDTO packageCode, @RequestParam(required = false, defaultValue = "true") Boolean isMoney);

    @GetMapping
    ResponseEntity<Object> list(@RequestParam(required = false) String packageProfileId,
                                @RequestParam(required = false) PartnerPackageSubscriptionStatus status, Pageable pageable);
}
