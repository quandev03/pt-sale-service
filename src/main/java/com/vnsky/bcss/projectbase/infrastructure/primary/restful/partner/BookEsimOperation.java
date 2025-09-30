package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner;

import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.EsimBookingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "eSIM Booking", description = "eSIM Booking APIs")
@RequestMapping("${application.path.base.public}/esim")
public interface BookEsimOperation {
    @PostMapping("/book")
    @Operation(summary = "Book eSIM", description = "Book eSIM with specified quantity and package for multiple requests")
    ResponseEntity<Object> bookEsim(@Validated @RequestBody EsimBookingRequest bookingRequest);

    @GetMapping("")
    @Operation(summary = "Search Book eSIM", description = "Get list eSIM bookings")
    @Parameter(name = "size", example = "20")
    @Parameter(name = "page", example = "0")
    ResponseEntity<Object> searchBookEsimList(
        @Parameter(hidden = true) @PageableDefault Pageable pageable,
        @RequestParam(name = "from", required = false) String dateFrom,
        @RequestParam(name = "to", required = false) String dateTo,
        @RequestParam(name = "status", required = false) Integer status,
        @RequestParam(name= "textSearch", required = false) String textSearch
    );

    @GetMapping("/{id}")
    @Operation(summary = "Find Book eSIM by ID", description = "Get eSIM booking details by ID including sale order lines and note")
    ResponseEntity<Object> findById(@PathVariable String id);

    @GetMapping("/get-package")
    ResponseEntity<Object> freePackageProfile();

    @PostMapping("/export")
    @Operation(summary = "Search Book eSIM", description = "Export list eSIM bookings")
    ResponseEntity<Object> export(
        @RequestParam(name = "from", required = false) String dateFrom,
        @RequestParam(name = "to", required = false) String dateTo,
        @RequestParam(name = "status", required = false) Integer status,
        @RequestParam(name= "textSearch", required = false) String textSearch
    );
}
