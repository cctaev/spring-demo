package com.cctaev.springbootsecuritydemo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
public class JwtService {
    private final String secretKey = "Changeme123";

    private final long accessTokenExpire = 1000L;

    public String getToken(UserInfoVo userInfo) {
        LocalDateTime expireDate = LocalDateTime.now().plusSeconds(accessTokenExpire);
        return JWT.create()
                .withClaim("userId", userInfo.getUserId())
                .withClaim("username", userInfo.getUsername())
                .withClaim("roleIds", userInfo.getRoleIds())
                .withClaim("registry", userInfo.isRegistry())
                .withExpiresAt(expireDate.atZone(ZoneId.systemDefault()).toInstant())
                .sign(Algorithm.HMAC256(secretKey));
    }

    public DecodedJWT verifyToken(String token) {
        return JWT.require(Algorithm.HMAC256(secretKey))
                .build().verify(token);
    }

    public UserInfoVo getUserInfo(DecodedJWT decodedJWT) {
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setUserId(decodedJWT.getClaim("userId").asString());
        userInfoVo.setUsername(decodedJWT.getClaim("username").asString());
        userInfoVo.setRoleIds(decodedJWT.getClaim("roleIds").asList(String.class));
        userInfoVo.setRegistry(decodedJWT.getClaim("registry").asBoolean());
        return userInfoVo;
    }
}
