package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner;

import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.BookEsimRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "eSIM Booking", description = "eSIM Booking APIs")
@RequestMapping("${application.path.base.private}/esim")
public interface BookEsimOperation {
    @PostMapping("/book")
    @Operation(summary = "Book eSIM", description = "Book eSIM with specified quantity and package")
    ResponseEntity<Object> bookEsim(@Validated @RequestBody BookEsimRequest request);

}
