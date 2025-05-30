package com.example.backend.entity;

/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Integer id;
    private String username,account,password,avatar,email;
    private Date time;
}
