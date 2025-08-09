package com.example.backend.controller.user;

import com.example.backend.pojo.RestBean;
import com.example.backend.pojo.entity.User;
import com.example.backend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Resource
    UserService userService;

    @GetMapping("/getUserById")
    public RestBean<User>getUserById(HttpServletRequest request)
    {
        User user=userService.getUserById((Integer) request.getAttribute("id"));
        user.setPassword("*");
        return RestBean.success("cg",user);
    }
}
