package com.example.resttemplatedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
@RequestMapping("/")
public class RestTemplateDemoApplication {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(RestTemplateDemoApplication.class, args);
    }


    @GetMapping("/user/{userId}")
    public Map<String, Object> getUserById(@PathVariable String userId) {
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", userId);
        return userInfo;
    }

    @GetMapping("/user/all")
    public List<Map<String, Object>> getAllUser() {
        HashMap<String, Object> tom = new HashMap<>();
        tom.put("userId", "tom");
        HashMap<String, Object> rose = new HashMap<>();
        rose.put("userId", "rose");
        List<Map<String, Object>> allUser = new ArrayList<>();
        allUser.add(tom);
        allUser.add(rose);
        return allUser;
    }
}
