package com.vnsky.bcss.projectbase.infrastructure.primary.restful;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("${application.path.base.public}/contracts-management")
public interface ContractOperation {
    @PostMapping("/ocr-data")
    ResponseEntity<Object> ocr(
        @RequestParam(name = "typeCard") int typeCard,
        @RequestPart MultipartFile front,
        @RequestPart MultipartFile back,
        @RequestPart MultipartFile portrait);

    @PostMapping("/gen-contract")
    ResponseEntity<Object> genContract(@RequestPart MultipartFile template) throws Exception;

}
