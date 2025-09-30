package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner;

import com.vnsky.bcss.projectbase.domain.dto.SaleOrderDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.BookEsimRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "eSIM Booking", description = "eSIM Booking APIs")
@RequestMapping("${application.path.base.public}/esim-free")
public interface BookEsimFreeOperation {
    @PostMapping("/book")
    @Operation(summary = "Book eSIM Free", description = "Book free eSIM with specified quantity and package for multiple requests")
    ResponseEntity<Object> bookEsim(@Validated @RequestBody List<BookEsimRequest> requests);

    @GetMapping("/book-free")
    @Operation(summary = "Book eSIM Free", description = "Get List book eSim free")
    @Parameter(name = "size", example = "10")
    @Parameter(name = "page", example = "0")
    ResponseEntity<Page<SaleOrderDTO>> getBookEsimFree(
        @Parameter(hidden = true) @PageableDefault Pageable pageable,
        @RequestParam(name = "from", required = false) String dateFrom,
        @RequestParam(name = "to", required = false) String dateTo,
        @RequestParam(name = "status", required = false) Integer status,
        @RequestParam(name= "textSearch", required = false) String textSearch,
        @RequestParam(name = "isFree", required = false,defaultValue = "0") Integer isFree
    );

    @GetMapping("/get-package")
    ResponseEntity<Object> freePackageProfile();

}
