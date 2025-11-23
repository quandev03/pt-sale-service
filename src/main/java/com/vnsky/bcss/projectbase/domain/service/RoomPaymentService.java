package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.*;
import com.vnsky.bcss.projectbase.domain.port.primary.RoomPaymentServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUnitRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUserRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.RoomPaymentRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.RoomServiceRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.external.MailServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.external.VietQRPort;
import com.vnsky.bcss.projectbase.domain.service.BankService;
import com.vnsky.bcss.projectbase.infrastructure.data.response.BankResponse;
import com.vnsky.bcss.projectbase.domain.port.secondary.external.mail.MailInfoDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.RoomServiceUsageDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.response.VietQRResponse;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomServiceType;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.Status;
import com.vnsky.bcss.projectbase.shared.utils.XlsxUtils;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomPaymentService implements RoomPaymentServicePort {

    private final RoomPaymentRepoPort roomPaymentRepoPort;
    private final RoomServiceRepoPort roomServiceRepoPort;
    private final OrganizationUnitRepoPort organizationUnitRepoPort;
    private final OrganizationUserRepoPort organizationUserRepoPort;
    private final VietQRPort vietQRPort;
    private final MailServicePort mailServicePort;
    private final BankService bankService;

    private static final String LOG_PREFIX = "[RoomPaymentService]_";

    @Override
    @Transactional
    public List<RoomPaymentDTO> processExcelAndCreatePayments(MultipartFile file, Integer month, Integer year) {
        log.info("{}Processing Excel file for month: {}/{}, file: {}", LOG_PREFIX, month, year, file.getOriginalFilename());

        // Đọc Excel file
        List<RoomServiceUsageDTO> usageData;
        try {
            usageData = XlsxUtils.readExcel(file.getInputStream(), RoomServiceUsageDTO.class).getDataLines();
        } catch (IOException e) {
            log.error("{}Error reading Excel file: {}", LOG_PREFIX, e.getMessage(), e);
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("Lỗi đọc file Excel: " + e.getMessage())
                .build();
        }

        if (usageData == null || usageData.isEmpty()) {
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("File Excel không có dữ liệu")
                .build();
        }

        List<RoomPaymentDTO> createdPayments = new ArrayList<>();

        // Xử lý từng phòng
        for (RoomServiceUsageDTO usage : usageData) {
            if (StringUtils.isBlank(usage.getRoomCode())) {
                log.warn("{}Skipping row with empty room code", LOG_PREFIX);
                continue;
            }

            try {
                RoomPaymentDTO payment = processRoomPayment(usage, month, year);
                if (payment != null) {
                    createdPayments.add(payment);
                }
            } catch (Exception e) {
                log.error("{}Error processing room {}: {}", LOG_PREFIX, usage.getRoomCode(), e.getMessage(), e);
                // Continue với phòng tiếp theo
            }
        }

        log.info("{}Processed {} payments", LOG_PREFIX, createdPayments.size());
        return createdPayments;
    }

    private RoomPaymentDTO processRoomPayment(RoomServiceUsageDTO usage, Integer month, Integer year) {
        // Tìm organization unit theo roomCode
        OrganizationUnitDTO orgUnit = organizationUnitRepoPort.findByCode(usage.getRoomCode())
            .orElseThrow(() -> {
                log.error("{}Organization unit not found for room code: {}", LOG_PREFIX, usage.getRoomCode());
                return BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED)
                    .message("Không tìm thấy phòng với mã: " + usage.getRoomCode())
                    .build();
            });

        // Kiểm tra đã có thanh toán cho tháng/năm này chưa
        Optional<RoomPaymentDTO> existing = roomPaymentRepoPort.findByOrgUnitIdAndMonthAndYear(
            orgUnit.getId(), month, year);
        if (existing.isPresent()) {
            log.warn("{}Payment already exists for room {} month {}/{}, skipping", 
                LOG_PREFIX, usage.getRoomCode(), month, year);
            return existing.get();
        }

        // Lấy tất cả dịch vụ phòng
        List<RoomServiceDTO> roomServices = roomServiceRepoPort.findByOrgUnitId(orgUnit.getId());

        // Tính toán tiền
        List<RoomPaymentDetailDTO> details = calculatePaymentDetails(usage, roomServices, orgUnit);
        BigDecimal totalAmount = details.stream()
            .map(RoomPaymentDetailDTO::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tạo QR code
        String qrCodeUrl = null;
        byte[] qrCodeImage = null;
        if (orgUnit.getOrgBankAccountNo() != null && orgUnit.getBankName() != null) {
            String bankCode = extractBankCode(orgUnit.getBankName());
            if (bankCode == null) {
                log.warn("{}Bank code not found for bank: {}", LOG_PREFIX, orgUnit.getBankName());
            } else {
                String content = String.format("Phòng %s đóng tiền phòng tháng %d/%d", 
                    usage.getRoomCode(), month, year);
                
                VietQRResponse qrResponse = vietQRPort.generateQRCode(
                    orgUnit.getOrgBankAccountNo(),
                    orgUnit.getOrgName() != null ? orgUnit.getOrgName() : "",
                    totalAmount,
                    content,
                    bankCode
                );

                if (qrResponse != null && qrResponse.getData() != null) {
                    qrCodeUrl = qrResponse.getData().getQrDataURL();
                    log.info("{}QR code URL generated: {}", LOG_PREFIX, qrCodeUrl);
                    
                    // Download QR code image từ URL
                    if (qrCodeUrl != null && !qrCodeUrl.isEmpty()) {
                        try {
                            qrCodeImage = downloadImageFromUrl(qrCodeUrl);
                            log.info("{}QR code image downloaded, size: {} bytes", LOG_PREFIX, 
                                qrCodeImage != null ? qrCodeImage.length : 0);
                        } catch (Exception e) {
                            log.error("{}Error downloading QR code image from URL: {}", LOG_PREFIX, 
                                e.getMessage(), e);
                            // Tiếp tục với qrCodeUrl, không fail toàn bộ process
                        }
                    }
                } else {
                    log.warn("{}Failed to generate QR code for room: {}", LOG_PREFIX, usage.getRoomCode());
                }
            }
        } else {
            log.warn("{}Missing bank information for room: {} (accountNo: {}, bankName: {})", 
                LOG_PREFIX, usage.getRoomCode(), 
                orgUnit.getOrgBankAccountNo(), orgUnit.getBankName());
        }

        // Lưu payment
        RoomPaymentDTO payment = RoomPaymentDTO.builder()
            .orgUnitId(orgUnit.getId())
            .month(month)
            .year(year)
            .totalAmount(totalAmount)
            .qrCodeUrl(qrCodeUrl)
            .qrCodeImage(qrCodeImage)
            .status(0) // Chưa thanh toán
            .build();

        log.info("{}Saving payment for room: {}, totalAmount: {}, qrCodeUrl: {}", 
            LOG_PREFIX, usage.getRoomCode(), totalAmount, qrCodeUrl);
        
        RoomPaymentDTO savedPayment = roomPaymentRepoPort.save(payment);
        
        log.info("{}Payment saved with ID: {}, qrCodeUrl: {}", 
            LOG_PREFIX, savedPayment.getId(), savedPayment.getQrCodeUrl());
        
        // Lưu details
        for (RoomPaymentDetailDTO detail : details) {
            detail.setRoomPaymentId(savedPayment.getId());
            roomPaymentRepoPort.saveDetail(detail);
        }

        // Load lại details để gửi email
        savedPayment.setDetails(roomPaymentRepoPort.findDetailsByRoomPaymentId(savedPayment.getId()));

        // Gửi email
        sendPaymentEmail(savedPayment, orgUnit, usage.getRoomCode());

        return savedPayment;
    }

    private List<RoomPaymentDetailDTO> calculatePaymentDetails(
        RoomServiceUsageDTO usage, 
        List<RoomServiceDTO> roomServices,
        OrganizationUnitDTO orgUnit) {
        
        List<RoomPaymentDetailDTO> details = new ArrayList<>();
        Map<RoomServiceType, RoomServiceDTO> serviceMap = roomServices.stream()
            .filter(rs -> rs.getStatus() != null && rs.getStatus().equals(Status.ACTIVE.getValue()))
            .collect(Collectors.toMap(RoomServiceDTO::getServiceType, rs -> rs, (a, b) -> a));

        // Tính tiền điện
        if (usage.getElectricityUsage() != null && usage.getElectricityUsage().compareTo(BigDecimal.ZERO) > 0) {
            RoomServiceDTO electricityService = serviceMap.get(RoomServiceType.ELECTRICITY);
            if (electricityService != null) {
                BigDecimal amount = usage.getElectricityUsage().multiply(electricityService.getPrice());
                details.add(createDetail(electricityService, usage.getElectricityUsage(), amount));
            }
        }

        // Tính tiền nước
        if (usage.getWaterUsage() != null && usage.getWaterUsage().compareTo(BigDecimal.ZERO) > 0) {
            RoomServiceDTO waterService = serviceMap.get(RoomServiceType.WATER);
            if (waterService != null) {
                BigDecimal amount = usage.getWaterUsage().multiply(waterService.getPrice());
                details.add(createDetail(waterService, usage.getWaterUsage(), amount));
            }
        }

        // Tính tiền xe - tìm dịch vụ có serviceCode chứa "XE" hoặc "VEHICLE"
        if (usage.getVehicleCount() != null && usage.getVehicleCount().compareTo(BigDecimal.ZERO) > 0) {
            RoomServiceDTO vehicleService = roomServices.stream()
                .filter(rs -> rs.getServiceCode() != null && 
                    (rs.getServiceCode().toUpperCase().contains("XE") || 
                     rs.getServiceCode().toUpperCase().contains("VEHICLE")))
                .findFirst()
                .orElse(null);

            if (vehicleService != null) {
                BigDecimal amount = usage.getVehicleCount().multiply(vehicleService.getPrice());
                details.add(createDetail(vehicleService, usage.getVehicleCount(), amount));
            }
        }

        // Thêm tiền phòng
        if (orgUnit.getPriceRoom() != null && orgUnit.getPriceRoom().compareTo(BigDecimal.ZERO) > 0) {
            RoomPaymentDetailDTO roomRentDetail = RoomPaymentDetailDTO.builder()
                .serviceType(RoomServiceType.ROOM_RENT)
                .serviceName("Tiền phòng")
                .quantity(BigDecimal.ONE)
                .unitPrice(orgUnit.getPriceRoom())
                .amount(orgUnit.getPriceRoom())
                .build();
            details.add(roomRentDetail);
        }

        // Thêm các dịch vụ khác (INTERNET, OTHER) nếu có
        for (RoomServiceDTO service : roomServices) {
            if (service.getServiceType() == RoomServiceType.INTERNET || 
                service.getServiceType() == RoomServiceType.OTHER) {
                // Dịch vụ cố định, không tính theo số lượng
                details.add(createDetail(service, BigDecimal.ONE, service.getPrice()));
            }
        }

        return details;
    }

    private RoomPaymentDetailDTO createDetail(RoomServiceDTO service, BigDecimal quantity, BigDecimal amount) {
        return RoomPaymentDetailDTO.builder()
            .roomServiceId(service.getId())
            .serviceType(service.getServiceType())
            .serviceName(service.getServiceName())
            .quantity(quantity)
            .unitPrice(service.getPrice())
            .amount(amount)
            .build();
    }

    private String extractBankCode(String bankName) {
        if (bankName == null || bankName.isEmpty()) {
            return null;
        }
        
        try {
            // Lấy danh sách ngân hàng từ API
            List<BankResponse> banks = bankService.getAllBanks();
            if (banks != null && !banks.isEmpty()) {
                String upperBankName = bankName.toUpperCase();
                // Tìm ngân hàng theo tên (case insensitive)
                for (BankResponse bank : banks) {
                    if ((bank.getName() != null && upperBankName.contains(bank.getName().toUpperCase())) ||
                        (bank.getShortName() != null && upperBankName.contains(bank.getShortName().toUpperCase())) ||
                        (bank.getNameEn() != null && upperBankName.contains(bank.getNameEn().toUpperCase()))) {
                        log.info("{}Found bank code: {} for bank: {}", LOG_PREFIX, bank.getCode(), bankName);
                        return bank.getCode();
                    }
                }
            }
        } catch (Exception e) {
            log.warn("{}Error getting bank list from API, using fallback mapping: {}", LOG_PREFIX, e.getMessage());
        }
        
        // Fallback: Map một số ngân hàng phổ biến với mã ngân hàng đúng
        Map<String, String> bankCodeMap = new HashMap<>();
        bankCodeMap.put("VIETCOMBANK", "970415");
        bankCodeMap.put("BIDV", "970418");
        bankCodeMap.put("VIETINBANK", "970415");
        bankCodeMap.put("AGRIBANK", "970405");
        bankCodeMap.put("TECHCOMBANK", "970407");
        bankCodeMap.put("ACB", "970416");
        bankCodeMap.put("VPBANK", "970432");
        bankCodeMap.put("TPBANK", "970423");
        bankCodeMap.put("MBBANK", "970422");
        bankCodeMap.put("SACOMBANK", "970403");
        bankCodeMap.put("NGÂN HÀNG TMCP NGOẠI THƯƠNG VIỆT NAM", "970415");
        bankCodeMap.put("NGÂN HÀNG TMCP ĐẦU TƯ VÀ PHÁT TRIỂN VIỆT NAM", "970418");
        bankCodeMap.put("NGÂN HÀNG TMCP CÔNG THƯƠNG VIỆT NAM", "970415");
        bankCodeMap.put("NGÂN HÀNG NÔNG NGHIỆP VÀ PHÁT TRIỂN NÔNG THÔN VIỆT NAM", "970405");
        
        // Tìm theo tên ngân hàng (case insensitive)
        String upperBankName = bankName.toUpperCase();
        for (Map.Entry<String, String> entry : bankCodeMap.entrySet()) {
            if (upperBankName.contains(entry.getKey())) {
                log.info("{}Using fallback bank code: {} for bank: {}", LOG_PREFIX, entry.getValue(), bankName);
                return entry.getValue();
            }
        }
        
        log.warn("{}Bank code not found for bank name: {}", LOG_PREFIX, bankName);
        return null;
    }

    private byte[] downloadImageFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        
        try {
            URL url = new URL(imageUrl);
            try (InputStream in = url.openStream()) {
                return in.readAllBytes();
            }
        } catch (IOException e) {
            log.error("{}Error downloading image from URL {}: {}", LOG_PREFIX, imageUrl, e.getMessage(), e);
            return null;
        }
    }

    private void sendPaymentEmail(RoomPaymentDTO payment, OrganizationUnitDTO orgUnit, String roomCode) {
        // Lấy danh sách user của phòng
        List<OrganizationUserDTO> users = organizationUserRepoPort.getAllOrganizationUserByOrgId(orgUnit.getId());
        
        if (users == null || users.isEmpty()) {
            log.warn("{}No users found for room: {}", LOG_PREFIX, roomCode);
            return;
        }

        // Lấy chi tiết payment
        List<RoomPaymentDetailDTO> details = roomPaymentRepoPort.findDetailsByRoomPaymentId(payment.getId());
        payment.setDetails(details);

        // Gửi email cho từng user
        for (OrganizationUserDTO user : users) {
            if (StringUtils.isBlank(user.getEmail())) {
                continue;
            }

            try {
                // Chuyển đổi details sang format cho email
                List<MailInfoDTO.RoomPaymentDetailInfo> detailInfos = details.stream()
                    .map(detail -> MailInfoDTO.RoomPaymentDetailInfo.builder()
                        .serviceName(detail.getServiceName())
                        .quantity(detail.getQuantity() != null ? detail.getQuantity().toString() : "0")
                        .unitPrice(detail.getUnitPrice() != null ? detail.getUnitPrice().toString() : "0")
                        .amount(detail.getAmount() != null ? detail.getAmount().toString() : "0")
                        .build())
                    .collect(java.util.stream.Collectors.toList());

                // Tạo MailInfoDTO với thông tin cần thiết
                MailInfoDTO mailInfo = MailInfoDTO.builder()
                    .to(user.getEmail())
                    .subject(String.format("Hóa đơn dịch vụ phòng %s - Tháng %d/%d", 
                        roomCode, payment.getMonth(), payment.getYear()))
                    .amount(payment.getTotalAmount().toString())
                    .name(user.getUserFullname() != null ? user.getUserFullname() : "")
                    .urlQR(payment.getQrCodeUrl())
                    .roomCode(roomCode)
                    .month(payment.getMonth())
                    .year(payment.getYear())
                    .roomPaymentDetails(detailInfos)
                    .build();

                mailServicePort.sendMail(mailInfo, "RoomPaymentInvoice");
                log.info("{}Email sent to: {}", LOG_PREFIX, user.getEmail());
            } catch (Exception e) {
                log.error("{}Error sending email to {}: {}", LOG_PREFIX, user.getEmail(), e.getMessage(), e);
            }
        }
    }

    @Override
    public RoomPaymentDTO getById(String id) {
        RoomPaymentDTO payment = roomPaymentRepoPort.findById(id)
            .orElseThrow(() -> {
                log.error("{}Room payment not found: {}", LOG_PREFIX, id);
                return BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED)
                    .message("Không tìm thấy thông tin thanh toán")
                    .build();
            });
        // Load details
        List<RoomPaymentDetailDTO> details = roomPaymentRepoPort.findDetailsByRoomPaymentId(payment.getId());
        payment.setDetails(details);
        return payment;
    }

    @Override
    public List<RoomPaymentDTO> getAll(String orgUnitId, Integer year, Integer month) {
        List<RoomPaymentDTO> payments;
        
        if (orgUnitId != null) {
            // Lấy theo orgUnitId
            payments = roomPaymentRepoPort.findByFilters(orgUnitId, year, month);
        } else {
            // Lấy tất cả payments của client hiện tại
            String clientId = SecurityUtil.getCurrentClientId();
            payments = roomPaymentRepoPort.findByClientId(clientId, year, month);
        }
        
        // Load details cho mỗi payment
        payments.forEach(payment -> {
            List<RoomPaymentDetailDTO> details = roomPaymentRepoPort.findDetailsByRoomPaymentId(payment.getId());
            payment.setDetails(details);
        });
        
        return payments;
    }
}

