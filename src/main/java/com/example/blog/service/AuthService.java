package com.example.blog.service;

import com.example.blog.dto.auth.LoginDTO;
import com.example.blog.dto.auth.UpdatePasswordDTO;
import com.example.blog.dto.auth.UpdateProfileDTO;
import com.example.blog.vo.LoginUserVO;
import com.example.blog.vo.LoginVO;
import com.example.blog.vo.UserProfileVO;

public interface AuthService {

    LoginVO login(LoginDTO loginDTO);

    LoginUserVO getLoginUser(String token);

    void logout(String token);

    UserProfileVO getCurrentUserProfile();

    void updateCurrentUserProfile(UpdateProfileDTO updateProfileDTO);

    void updatePassword(UpdatePasswordDTO updatePasswordDTO);
}
