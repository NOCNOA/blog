package com.example.blog.service.impl;

import com.example.blog.common.exception.BusinessException;
import com.example.blog.common.result.ResultCode;
import com.example.blog.dto.auth.LoginDTO;
import com.example.blog.dto.auth.UpdatePasswordDTO;
import com.example.blog.dto.auth.UpdateProfileDTO;
import com.example.blog.entity.User;
import com.example.blog.mapper.UserMapper;
import com.example.blog.service.AuthService;
import com.example.blog.util.JwtUtil;
import com.example.blog.util.LoginUserContext;
import com.example.blog.vo.LoginUserVO;
import com.example.blog.vo.LoginVO;
import com.example.blog.vo.UserProfileVO;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserMapper userMapper, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "用户名或密码错误");
        }
        if (!matchesPassword(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "当前用户已被禁用");
        }

        upgradePasswordIfNecessary(user, loginDTO.getPassword());

        String token = jwtUtil.generateToken(user.getId());
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserInfo(toLoginUserVO(user));
        return loginVO;
    }

    @Override
    public LoginUserVO getLoginUser(String token) {
        Long userId = parseUserId(token);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }
        return toLoginUserVO(user);
    }

    @Override
    public void logout(String token) {
    }

    @Override
    public UserProfileVO getCurrentUserProfile() {
        return toUserProfileVO(getCurrentUser());
    }

    @Override
    public void updateCurrentUserProfile(UpdateProfileDTO updateProfileDTO) {
        User user = getCurrentUser();
        userMapper.updateProfileById(user.getId(), updateProfileDTO.getNickname(), updateProfileDTO.getAvatar(), updateProfileDTO.getEmail());
    }

    @Override
    public void updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        User user = getCurrentUser();
        if (!matchesPassword(updatePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "原密码不正确");
        }
        if (updatePasswordDTO.getOldPassword().equals(updatePasswordDTO.getNewPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "新密码不能与原密码相同");
        }
        userMapper.updatePasswordById(user.getId(), passwordEncoder.encode(updatePasswordDTO.getNewPassword()));
    }

    private Long parseUserId(String token) {
        try {
            return jwtUtil.parseUserId(token);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "登录已失效");
        }
    }

    private boolean matchesPassword(String rawPassword, String storedPassword) {
        if (!StringUtils.hasText(storedPassword)) {
            return false;
        }
        if (isEncodedPassword(storedPassword)) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return storedPassword.equals(rawPassword);
    }

    private void upgradePasswordIfNecessary(User user, String rawPassword) {
        if (user == null || !StringUtils.hasText(user.getPassword())) {
            return;
        }
        if (isEncodedPassword(user.getPassword())) {
            return;
        }
        userMapper.updatePasswordById(user.getId(), passwordEncoder.encode(rawPassword));
    }

    private boolean isEncodedPassword(String password) {
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }

    private LoginUserVO toLoginUserVO(User user) {
        LoginUserVO loginUserVO = new LoginUserVO();
        loginUserVO.setId(user.getId());
        loginUserVO.setUsername(user.getUsername());
        loginUserVO.setNickname(user.getNickname());
        loginUserVO.setAvatar(user.getAvatar());
        return loginUserVO;
    }

    private UserProfileVO toUserProfileVO(User user) {
        UserProfileVO userProfileVO = new UserProfileVO();
        userProfileVO.setId(user.getId());
        userProfileVO.setUsername(user.getUsername());
        userProfileVO.setNickname(user.getNickname());
        userProfileVO.setAvatar(user.getAvatar());
        userProfileVO.setEmail(user.getEmail());
        return userProfileVO;
    }

    private User getCurrentUser() {
        Long userId = LoginUserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "登录已失效");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }
        return user;
    }
}
