package com.example.backend.pojo.entity;

/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    private String username,account,password,avatar,email;
    private Integer time;
}
