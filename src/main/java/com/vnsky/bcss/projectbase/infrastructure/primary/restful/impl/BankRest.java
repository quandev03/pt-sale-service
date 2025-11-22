package com.vnsky.bcss.projectbase.infrastructure.primary.restful.impl;

import com.vnsky.bcss.projectbase.domain.service.BankService;
import com.vnsky.bcss.projectbase.infrastructure.data.response.BankResponse;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.BankOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BankRest implements BankOperation {

    private final BankService bankService;

    @Override
    public ResponseEntity<List<BankResponse>> getAllBanks() {
        List<BankResponse> banks = bankService.getAllBanks();
        return ResponseEntity.ok(banks);
    }
}

