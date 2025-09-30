package com.vnsky.bcss.projectbase.infrastructure.primary.restful;

import com.vnsky.bcss.projectbase.domain.dto.IsdnTransactionDTO;
import com.vnsky.bcss.projectbase.domain.dto.UploadNumberMetadataDTO;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.common.constant.ExtendedMediaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Tag(name = "Upload number", description = "API liên quan đến upload số")
@RequestMapping("${application.path.base.private}/upload-number")
public interface UploadNumberOperation {
    @GetMapping
    @Operation(summary = "Find Upload number resource")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
        examples = {
            @ExampleObject(name = "This is sample response send find Upload number resource",
                summary = "Sample Response",
                value = Constant.EMPTY_STRING)
        }
    ))
    ResponseEntity<Page<IsdnTransactionDTO>> find(
        @Parameter(description = "từ ngày")
        @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromTime,
        @Parameter(description = "đến ngày")
        @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toTime,
        @ParameterObject @PageableDefault Pageable pageable);

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Upload number")
    ResponseEntity<IsdnTransactionDTO> submit(
        @RequestPart(value = "numberFile", required = false) MultipartFile numberFile,
        @Parameter(example = """
            {
              "description": "Mô tả thêm về upload số"
            }
            """) @RequestPart("metadata") UploadNumberMetadataDTO metadata);

    @GetMapping("/{id}")
    @Operation(summary = "Fet a Transaction")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
        examples = {
            @ExampleObject(name = "This is sample response send get detail upload number resource",
                summary = "Sample Response",
                value = Constant.EMPTY_STRING)
        }
    ))
    ResponseEntity<IsdnTransactionDTO> get(
        @Parameter(description = "ID of upload transaction")
        @PathVariable("id") String transactionId);

    @GetMapping(value = "/samples/csv", produces = ExtendedMediaType.TEXT_CSV_VALUE)
    ResponseEntity<Resource> getSampleCsv();

    @GetMapping(value = "/samples/xlsx", produces = ExtendedMediaType.APPLICATION_XLSX_VALUE)
    ResponseEntity<Resource> getSampleXlsx();
}
