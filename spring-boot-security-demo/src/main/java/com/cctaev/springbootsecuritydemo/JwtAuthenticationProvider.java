package com.cctaev.springbootsecuritydemo;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * desc
 *
 * @author cctaev
 * @since 2024/10/29
 */
@Slf4j
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
    @Resource
    private JwtService jwtService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthentication auth = (JwtAuthentication) authentication;
        String jwt = auth.getCredentials();
        try {
            DecodedJWT decodedJWT = jwtService.verifyToken(jwt);
            Date expiresAt = decodedJWT.getExpiresAt();
            if (new Date().after(expiresAt)) {
                log.info("凭证过期");
                return auth;
            }
            UserInfoVo userInfoVo = jwtService.getUserInfo(decodedJWT);
            return new JwtAuthentication(userInfoVo);
        } catch (Exception e) {
            log.error("凭证无效");
            return auth;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthentication.class.isAssignableFrom(authentication);
    }
}
