package com.example.resttemplatedemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试使用RestTemplate
 *
 * 注意：运行UC前，请先启动微服务
 */
@SpringBootTest
class RestTemplateDemoApplicationTests {
    private static String GET_ALL_USER_URL = "http://localhost:8080/user/all";

    private static String GET_USER_BY_ID_URL = "http://localhost:8080/user/{userId}";
    private static String ADD_USER_URL = "http://localhost:8080/user";
    private static String MODIFY_USER_URL = "http://localhost:8080/user";
    private static String DELETE_USER_BY_ID_URL = "http://localhost:8080/user/{userId}";

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 尝试发送GET请求
     */
    @Test
    void test_send_get_request() {
        ResponseEntity<List> resultEntity = restTemplate.getForEntity(GET_ALL_USER_URL, List.class);
        MediaType contentType = resultEntity.getHeaders().getContentType();
        System.out.println(contentType);
        System.out.println(resultEntity.getBody());

        // 可以看出，虽然getForObject支持将响应体转换为POJO，但是不支持泛型
        // 如果想支持泛型，那么就需要使用exchange方法并配合ParameterizedTypeReference
        // 所以getForObject以及getForEntity只适合返回String类型的JSON，然后在后面自己处理JSON。
        List resultObject = restTemplate.getForObject(GET_ALL_USER_URL, List.class);
        System.out.println(resultObject);
    }


    /**
     * 将响应结果转换为对于的泛型类型，只能使用exchange
     */
    @Test
    void test_extract_source_by_generic_type() throws URISyntaxException {
        ParameterizedTypeReference<List<Map<String, Object>>> typeReference =
                new ParameterizedTypeReference<List<Map<String, Object>>>() {
                };
        RequestEntity<Object> requestEntity = new RequestEntity<>(HttpMethod.GET, new URI(GET_ALL_USER_URL));
        ResponseEntity<List<Map<String, Object>>> responseEntity = restTemplate.exchange(requestEntity, typeReference);
        List<Map<String, Object>> responseBody = responseEntity.getBody();
        System.out.println(responseBody);
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

    /**
     * 尝试发送带路径参数的请求
     */
    @Test
    void test_request_with_path_variable() {
        String userId = "mike";
        String responseBody = restTemplate.getForObject(GET_USER_BY_ID_URL, String.class, userId);
        System.out.println(responseBody);
    }

    /**
     * 尝试使用exchange方法
     * <p>
     * 有一个规律：如果要想使用路径参数，那么就要使用URL为String类型的充值方法；否则的话，可以简单的使用URL类型为URI的重载方法。
     * 但是这样太麻烦了，简单点，只用exchange的两种重载方法就足够了
     * <T> ResponseEntity<T> exchange(RequestEntity<?> entity, Class<T> responseType)
     * <T> ResponseEntity<T> exchange(RequestEntity<?> entity, ParameterizedTypeReference<T> responseType)
     */
    @Test
    void test_request_by_exchange() throws URISyntaxException {
        String userId = "tom";
        UriTemplateHandler uriTemplateHandler = restTemplate.getUriTemplateHandler();
        // 发送get请求
        URI getUserByIdUri = uriTemplateHandler.expand(GET_USER_BY_ID_URL, userId);
        RequestEntity<Object> getEntity = new RequestEntity<>(HttpMethod.GET, getUserByIdUri);
        ResponseEntity<String> getResponse = restTemplate.exchange(getEntity, String.class);
        System.out.println(getResponse);
        System.out.println("----------\n");

        // 发送post请求
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", "Rose");
        RequestEntity<HashMap<String, Object>> postEntity = new RequestEntity<>(userInfo, HttpMethod.POST, new URI(ADD_USER_URL));
        ResponseEntity<String> postResponse = restTemplate.exchange(postEntity, String.class);
        System.out.println(postResponse);
        System.out.println("----------\n");

        // 发送put请求
        // 不管有没有路径参数，都可以走一遍这个方法
        URI modifyUseUri = uriTemplateHandler.expand(MODIFY_USER_URL);
        RequestEntity<Object> putEntity = new RequestEntity<>(userInfo, HttpMethod.PUT, modifyUseUri);
        ResponseEntity<String> putResponse = restTemplate.exchange(putEntity, String.class);
        System.out.println(putResponse);
        System.out.println("----------\n");

        // 发送delete请求
        URI deleteUserByIdUri = uriTemplateHandler.expand(DELETE_USER_BY_ID_URL, userId);
        RequestEntity<Object> deleteEntity = new RequestEntity<>(HttpMethod.DELETE, deleteUserByIdUri);
        ResponseEntity<String> deleteResponse = restTemplate.exchange(deleteEntity, String.class);
        System.out.println(deleteResponse);
        System.out.println("----------\n");
    }
}
