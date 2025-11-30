package com.vnsky.bcss.projectbase.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnsky.bcss.projectbase.domain.dto.*;
import com.vnsky.bcss.projectbase.domain.port.primary.AgentDebitServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.OrganizationUnitServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.AgentDebitRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.external.IntegrationPort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.AgentDebitRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.BaseIntegrationRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.AgentDebitResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.partner.TotalAmountResponse;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.utils.DateUtils;
import com.vnsky.common.exception.domain.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentDebitService implements AgentDebitServicePort {
    private final ObjectMapper objectMapper;
    private final IntegrationPort integrationPort;
    private final AgentDebitRepoPort agentDebitRepoPort;
    private final OrganizationUnitServicePort organizationUnitServicePort;

    private static final Integer FIRST_OBJECT = 0;
    private static final String CMD = "MBF";
    private static final String TYPE = "GET_VOUCHER_INFO";
    private static final String CODE_FAIL = "API001";

    @Override
    public Page<AgentDebitDTO> search(String q, String type, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        String orgId = organizationUnitServicePort.getOrgCurrent().getId();
        return agentDebitRepoPort.search(q, type, orgId, startDate, endDate, pageable);
    }

    @Override
    @Transactional
    public AgentDebitDTO addAgentDebit(String voucherCode, String voucherType) {
        // Check nếu paymentID đã tồn tại
        OrganizationUnitDTO organizationUnitDTO = organizationUnitServicePort.getOrgCurrent();
        if (agentDebitRepoPort.existsByPaymentId(voucherCode, organizationUnitDTO.getId())) {
            throw BaseException.badRequest(ErrorCode.AGENT_DEBIT_VOUCHER_DUPLICATE).build();
        }

        // Gọi integration service để lấy số tiền nạp
        AgentDebitResponse response = getVoucherInfo(organizationUnitDTO.getOrgCode(), voucherCode, voucherType);
        if (Objects.isNull(response.getBody()) || CODE_FAIL.equals(response.getCode())) {
            log.info("[GET_TOPUP]_Proof of payment does not exist or has expired");
            throw BaseException.badRequest(ErrorCode.AGENT_DEBIT_PAYMENT_DOCUMENT_NOT_FOUND).build();
        }

        Long amountLong = safeParseLongOrZero(response.getBody().getAmount());

        // Update công nợ hiện tại
        organizationUnitDTO.setDebtLimit(Optional.ofNullable(organizationUnitDTO.getDebtLimit()).orElse(0L) + amountLong);
        organizationUnitDTO.setDebtLimitMbf(Optional.ofNullable(organizationUnitDTO.getDebtLimitMbf()).orElse(0L) + amountLong);

        organizationUnitServicePort.save(organizationUnitDTO, organizationUnitDTO.getId(), false);

        // Tạo bản ghi lịch sử trong AgentDebit
        AgentDebitDTO agentDebit = AgentDebitDTO.builder()
            .orgId(organizationUnitDTO.getId())
            .clientId(organizationUnitDTO.getClientId())
            .debtLimit(organizationUnitDTO.getDebtLimit())
            .debtLimitMbf(organizationUnitDTO.getDebtLimitMbf())
            .amount(amountLong)
            .paymentId(voucherCode)
            .type(voucherType)
            .build();

        return agentDebitRepoPort.save(agentDebit);
    }

    private AgentDebitResponse getVoucherInfo(String orgCode, String voucherCode, String voucherType) {
        AgentDebitRequest agentDebitRequest = AgentDebitRequest.builder()
            .shopCode(orgCode)
            .voucherCode(voucherCode)
            .voucherType(voucherType)
            .build();

        BaseIntegrationRequest request = BaseIntegrationRequest.builder()
            .cmd(CMD)
            .type(TYPE)
            .data(agentDebitRequest)
            .build();

        GeneralDTO res = integrationPort.executeRequest(request, GeneralDTO.class);
        return objectMapper.convertValue(
            ((List<?>) res.getData()).get(FIRST_OBJECT),
            AgentDebitResponse.class
        );
    }

    @Override
    public ByteArrayResource export(String q, String type, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        OrganizationUnitDTO organizationUnitDTO = organizationUnitServicePort.getOrgCurrent();
        Page<AgentDebitDTO> responses = agentDebitRepoPort.search(q, type, organizationUnitDTO.getId(), startDate, endDate, pageable);
        TotalAmountResponse debit = getTotalAmount();
        ClassPathResource resource = new ClassPathResource("templates/BHTT-ddmmyyyyhhmmss.xlsx");
        try (InputStream inputStream = resource.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream);
             ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {

            Sheet sheet = workbook.getSheetAt(0);

            String titleDate = String.format("Từ ngày %s - Đến ngày %s", DateUtils.localDateToString(startDate, "dd/MM/yyyy"), DateUtils.localDateToString(endDate, "dd/MM/yyyy"));
            // Start date và end date
            setCell(sheet, 2, 0, titleDate);
            // Tổng tiền đã nạp
            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
            String totalAmount = String.format("Tổng tiền đã nạp: %s VNĐ", nf.format(Optional.ofNullable(debit.getTotalAmount()).orElse(0L)));
            setCell(sheet, 3, 0, totalAmount);

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.setAlignment(HorizontalAlignment.CENTER);
            centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            centerStyle.setBorderBottom(BorderStyle.THIN);
            centerStyle.setBorderTop(BorderStyle.THIN);
            centerStyle.setBorderLeft(BorderStyle.THIN);
            centerStyle.setBorderRight(BorderStyle.THIN);

            // Dòng dữ liệu: bắt đầu từ row 6 (index = 5)
            int rowIndex = 5;
            int stt = 1;
            for (AgentDebitDTO item : responses.getContent()) {
                Row dataRow = sheet.createRow(rowIndex++);

                Cell cell0 = dataRow.createCell(0);
                cell0.setCellValue(stt++);
                cell0.setCellStyle(centerStyle);

                Cell cell1 = dataRow.createCell(1);
                cell1.setCellValue(item.getPaymentId());
                cell1.setCellStyle(centerStyle);

                Cell cell2 = dataRow.createCell(2);
                cell2.setCellValue(getLabel(item.getType()));
                cell2.setCellStyle(centerStyle);

                Cell cell3 = dataRow.createCell(3);
                cell3.setCellValue(nf.format(Optional.ofNullable(item.getAmount()).map(Long::doubleValue).orElse(0.0)));
                cell3.setCellStyle(centerStyle);

                Cell cell4 = dataRow.createCell(4);
                cell4.setCellValue(nf.format(Optional.ofNullable(item.getDebtLimit()).map(Long::doubleValue).orElse(0.0)));
                cell4.setCellStyle(centerStyle);

                Cell cell5 = dataRow.createCell(5);
                cell5.setCellValue(item.getCreatedBy());
                cell5.setCellStyle(centerStyle);

                Cell cell6 = dataRow.createCell(6);
                cell6.setCellValue(Objects.requireNonNull(
                    DateUtils.localDateTimeToString(item.getCreatedDate(), "dd/MM/yyyy HH:mm:ss")));
                cell6.setCellStyle(centerStyle);
            }
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        } catch (IOException e) {
            log.error("[AGENT_DEBIT]: Error while exporting Excel file");
        }
        return null;
    }

    @Override
    public TotalAmountResponse getTotalAmount() {
        OrganizationUnitDTO organizationUnitDTO = organizationUnitServicePort.getOrgCurrent();
        List<AgentDebitDTO> responses = agentDebitRepoPort.findAll(organizationUnitDTO.getId());
        Long total = responses.stream()
            .map(AgentDebitDTO::getAmount)
            .filter(Objects::nonNull)
            .mapToLong(Long::longValue)
            .sum();
        return new TotalAmountResponse(total);
    }

    private static Long safeParseLongOrZero(String amount) {
        try {
            return (amount == null || amount.trim().isEmpty()) ? 0L : Long.parseLong(amount.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private void setCell(Sheet sheet, int rowIndex, int colIndex, String value) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        cell.setCellValue(value);
    }

    private static String getLabel(String type) {
        return switch (type) {
            case "1" -> "Tiền mặt";
            case "2" -> "Chuyển khoản";
            default -> "Không xác định";
        };
    }
}
