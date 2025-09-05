package com.example.backend.pojo.vo;

/*
 * @Auther:fz
 * @Date:2025/9/5
 * @Description:
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    private Integer id;
    private String username,account,password,avatar,email;
    private Integer time;
}
