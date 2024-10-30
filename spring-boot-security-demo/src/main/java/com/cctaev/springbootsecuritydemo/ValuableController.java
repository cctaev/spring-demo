package com.cctaev.springbootsecuritydemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * desc
 *
 * @author cctaev
 * @since 2024/10/29
 */
@RestController()
@RequestMapping("/valuable")
@Slf4j
public class ValuableController {

    @GetMapping("/key")
    public String hello() {
        SecurityContext context = SecurityContextHolder.getContext();
        JwtAuthentication auth = (JwtAuthentication) context.getAuthentication();
        UserInfoVo userInfoVo = auth.getUserInfoVo();
        log.info(userInfoVo.toString());
        return "Important Information!!!!";
    }
}
