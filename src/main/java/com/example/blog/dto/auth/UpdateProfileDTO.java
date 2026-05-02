package com.example.blog.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class UpdateProfileDTO {

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    private String avatar;
    private String email;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
}
