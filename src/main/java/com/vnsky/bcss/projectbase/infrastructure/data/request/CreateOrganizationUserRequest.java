package com.vnsky.bcss.projectbase.infrastructure.data.request;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrganizationUserRequest {

    @NonNull
    private String userId;

    private String username;

    private String userFullName;

    private String clientId;

    private Integer status;

    private String email;

    private List<String> organizationIds;
}
