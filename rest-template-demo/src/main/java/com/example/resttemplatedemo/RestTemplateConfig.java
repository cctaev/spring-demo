package com.example.resttemplatedemo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author cctaev
 * @since 2023-05-26
 */
@Configuration
public class RestTemplateConfig {
    /**
     * 支持HTTPS且信任任何证书。还支持代理
     * @return restTemplate
     */
    public RestTemplate restTemplateCustom() {
        RestTemplate restTemplate = new RestTemplate();
        return null;
    }
}
