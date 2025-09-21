package com.vnsky.bcss.projectbase.infrastructure.data.response;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoResponse {
    @DbColumnMapper("USER_ID")
    private String userId;

    @DbColumnMapper("USER_NAME")
    private String username;

    @DbColumnMapper("USER_FULLNAME")
    private String fullName;

    @DbColumnMapper("ORG_NAME")
    private String orgName;

    @DbColumnMapper("EMAIL")
    private String email;
}
