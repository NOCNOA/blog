package com.example.blog.vo;

public class LoginVO {

    private String token;
    private LoginUserVO userInfo;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LoginUserVO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(LoginUserVO userInfo) {
        this.userInfo = userInfo;
    }
}
