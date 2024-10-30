package com.cctaev.springbootsecuritydemo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * desc
 *
 * @author cctaev
 * @since 2024/10/29
 */
@RestController
@RequestMapping("/open")
public class PublicController {
    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }
}
