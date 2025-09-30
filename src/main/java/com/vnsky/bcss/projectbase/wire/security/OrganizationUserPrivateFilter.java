package com.vnsky.bcss.projectbase.wire.security;

import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.security.dto.ClientDTO;
import com.vnsky.security.dto.UserDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.simpleframework.xml.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
@Order()
public class OrganizationUserPrivateFilter extends OncePerRequestFilter {
    private static final String API_KEY_HEADER = "x-api-key";

    @Value("${third-party.admin.apikey}")
    private String apiKeyOrg;

    @Value("${application.internal-path.organization-user-private}")
    private String url;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader(API_KEY_HEADER);
        if(!request.getRequestURI().contains(url)) {
            filterChain.doFilter(request, response);
            return;
        }

        if(apiKey == null || !apiKey.equals(apiKeyOrg)){
            throw BaseException.badRequest(ErrorCode.API_KEY_NOT_CORRECT).build();
        }

        UserDTO systemUser = UserDTO.builder()
            .username(Constant.SYSTEM)
            .clientIdentity(Constant.VNSKY_CLIENT_ID)
            .client(ClientDTO.builder()
                    .id(Constant.VNSKY_CLIENT_ID)
                .build())
            .build();
        systemUser.setAttribute("preferredUsername", Constant.SYSTEM);

        Authentication auth = new PreAuthenticatedAuthenticationToken(systemUser, null, systemUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
