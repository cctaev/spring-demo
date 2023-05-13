package com.example.resttemplatedemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * 测试使用RestTemplate
 */
@SpringBootTest
class RestTemplateDemoApplicationTests {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    void test_send_get_request() {
        String url = "http://localhost:8080/user/all";

        ResponseEntity<List> resultEntity = restTemplate.getForEntity(url, List.class);
        MediaType contentType = resultEntity.getHeaders().getContentType();
        System.out.println(contentType);
        System.out.println(resultEntity.getBody());

        List resultObject = restTemplate.getForObject(url, List.class);
        System.out.println(resultObject);
    }

    /**
     * 观察RestTemplate的默认convertor和它们的支持媒体类型
     */
    @Test
    void test_default_converter_and_its_support_media() {
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> convertor : messageConverters) {
            System.out.println("Convertor: " + convertor.getClass());
            // 输出支持媒体类型
            List<MediaType> supportedMediaTypes = convertor.getSupportedMediaTypes();
            System.out.println("SupportedMediaType:");
            for (MediaType supportedMediaType : supportedMediaTypes) {
                System.out.println(supportedMediaType);
            }
            System.out.println("-----------------------------\n");
        }
    }

}
