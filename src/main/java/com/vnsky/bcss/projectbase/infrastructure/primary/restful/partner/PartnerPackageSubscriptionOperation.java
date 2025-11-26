package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner;

import com.vnsky.bcss.projectbase.infrastructure.data.request.PartnerPackageSubscriptionCreateRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.PartnerPackageSubscriptionPaymentRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.PartnerPackageSubscriptionBuyRequest;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Partner Package Subscription", description = "Quản lý gói dịch vụ đối tác đã mua")
@RequestMapping({
    "${application.path.base.private}/partner-package-subscriptions",
    "${application.path.base.public}/partner-package-subscriptions"
})
public interface PartnerPackageSubscriptionOperation {

    /**
     * API admin/private: tạo subscription với orgUnitId truyền trực tiếp.
     */
    @PostMapping
    ResponseEntity<Object> createSubscription(@RequestBody @Valid PartnerPackageSubscriptionCreateRequest request);

    /**
     * API web partner: mua gói cho tổ chức hiện tại (org được lấy từ context).
     */
    @PostMapping("/buy")
    ResponseEntity<Object> buySubscription(@RequestBody @Valid PartnerPackageSubscriptionBuyRequest request);

    /**
     * API admin/private: khởi tạo thanh toán subscription với orgUnitId truyền trực tiếp.
     */
    @PostMapping("/payments")
    ResponseEntity<Object> createSubscriptionPayment(@RequestBody @Valid PartnerPackageSubscriptionPaymentRequest request);

    /**
     * API web partner: khởi tạo thanh toán subscription cho tổ chức hiện tại.
     */
    @PostMapping("/payments/buy")
    ResponseEntity<Object> buySubscriptionPayment(@RequestBody @Valid PartnerPackageSubscriptionBuyRequest request);

    /**
     * API danh sách subscription (admin có thể filter theo org bất kỳ).
     */
    @GetMapping
    ResponseEntity<Object> listSubscriptions(
        @RequestParam(required = false) String organizationUnitId,
        @RequestParam(required = false) String packageProfileId,
        @RequestParam(required = false) PartnerPackageSubscriptionStatus status,
        @PageableDefault Pageable pageable
    );

    /**
     * API web partner: danh sách subscription của tổ chức hiện tại.
     */
    @GetMapping("/my-subscriptions")
    ResponseEntity<Object> listMySubscriptions(
        @RequestParam(required = false) String packageProfileId,
        @RequestParam(required = false) PartnerPackageSubscriptionStatus status,
        @PageableDefault Pageable pageable
    );

    /**
     * API web partner: danh sách gói cước có thể mua cho đối tác hiện tại.
     */
    @GetMapping("/available-packages")
    ResponseEntity<Object> listAvailablePackages();

    /**
     * Dừng subscription theo id.
     */
    @PostMapping("/{id}/stop")
    ResponseEntity<Object> stopSubscription(@PathVariable("id") String id);
}


