package com.cctaev.springbootsecuritydemo;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * desc
 *
 * @author cctaev
 * @since 2024/10/30
 */
public class JwtAuthentication extends AbstractAuthenticationToken {
    private String jwt;

    @Getter
    private UserInfoVo userInfoVo;

    public JwtAuthentication(String jwt) {
        super(null);
        this.jwt = jwt;
    }

    public JwtAuthentication(UserInfoVo userInfoVo) {
        super(null);
        this.userInfoVo = userInfoVo;
        setAuthenticated(true);
    }

    @Override
    public String getCredentials() {
        return this.jwt;
    }

    @Override
    public Object getPrincipal() {
        return this.userInfoVo.getUserId();
    }
}
