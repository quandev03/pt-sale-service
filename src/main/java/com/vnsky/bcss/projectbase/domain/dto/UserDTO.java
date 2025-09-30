package com.vnsky.bcss.projectbase.domain.dto;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(value = {"name", "attributes", "locked"}, allowGetters = true)
@FieldNameConstants
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4881046053049599618L;

    @DbColumnMapper("USER_ID")
    @Schema(title = "ID người dùng", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @Schema(title = "Tên đăng nhập người dùng", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~@#$!%^*?&()])(?=\\S+$).{6,}$", groups = {CreateCase.class})
    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, title = "Mật khẩu người dùng", pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[~@#$!%^*?&()])(?=\\\\S+$).{6,}$")
    private String password;

    @Schema(title = "Họ tên người dùng", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fullname;

    @Schema(title = "Ngày sinh người dùng")
    private Date dateOfBirth;

    @Schema(title = "Chức vụ người dùng")
    private String positionTitle;

    @Schema(title = "Trạng thái người dùng", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    private String type;

    private Integer loginMethod;

    @Schema(title = "Địa chỉ thư điện tử người dùng", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(title = "Số điện thoại người dùng")
    private String phoneNumber;

    @Schema(title = "Giới tính người dùng")
    private Integer gender;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Mật khẩu cần phải đổi")
    private boolean needChangePassword = false;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Thời gian hết hạn mật khẩu")
    private LocalDateTime passwordExpireTime;

    @JsonIgnore
    @Schema(hidden = true)
    private Integer loginFailedCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Các nhóm người dùng mà người dùng này thuộc về")
    private transient Set<Object> groups;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Các vai trò mà người dùng này thuộc về")
    private transient Set<Object> roles;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Các phòng ban mà người dùng này thuộc về")
    private transient Set<Object> departments;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, title = "Các id nhóm người dùng gán cho người dùng này")
    private Set<String> groupIds;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, title = "Các id vai trò gán cho người dùng này")
    private Set<String> roleIds;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, title = "Các id phòng ban gán cho người dùng này")
    private Set<Long> departmentIds;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, title = "Các id kho gán cho người dùng này")
    private Set<Long> stockIds;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "The client which this user belong to")
    private transient Object client;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Global qualified username")
    private String preferredUsername;

    @DbColumnMapper("ORG_NAME")
    private String orgName;

}
