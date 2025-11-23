package com.vnsky.bcss.projectbase.infrastructure.data.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request cập nhật thông tin người dùng thuộc tổ chức")
public class UpdateOrganizationUserRequest {

    private String id;

    @Schema(description = "ID đơn vị tổ chức")
    private String orgId;

    @Schema(description = "ID người dùng")
    private String userId;

    @Schema(description = "Tên đăng nhập")
    private String userName;

    @Schema(description = "Họ và tên đầy đủ")
    private String userFullname;

    @Email(message = "Email không đúng định dạng")
    @Schema(description = "Email")
    private String email;

    @Schema(description = "Trạng thái (1: Hoạt động, 0: Không hoạt động)")
    private Integer status;

    @Schema(description = "Đơn vị hiện tại (1: Có, 0: Không)")
    private Integer isCurrent;
}

