package com.example.backend.service;

/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */

import com.example.backend.entity.User;

public interface UserService {
    User getUserByAccountAndPassword(String account, String password);
    User getUserById(Integer id);
}
