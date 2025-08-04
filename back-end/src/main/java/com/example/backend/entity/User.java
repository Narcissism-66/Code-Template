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
    private Integer time;
    public User(){};

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public User(Integer id, String username, String account, String password, String avatar, String email, Integer time){
        this.id=id;
        this.username=username;
        this.account=account;
        this.password=password;
        this.avatar=avatar;
        this.email=email;
        this.time=time;
    }

}
