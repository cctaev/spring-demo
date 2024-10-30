package com.cctaev.springbootsecuritydemo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息
 *
 * @author cctaev
 * @since 2024/10/19
 */
@Data
@Accessors(chain = true)
public class UserInfoVo {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 是否注册过
     */
    private boolean registry;

    /**
     * 角色ID列表
     */
    private List<String> roleIds;

}
