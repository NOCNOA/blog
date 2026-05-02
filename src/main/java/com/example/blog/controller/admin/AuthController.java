package com.example.blog.controller.admin;

import com.example.blog.annotation.OperationLog;
import com.example.blog.common.result.Result;
import com.example.blog.dto.auth.LoginDTO;
import com.example.blog.dto.auth.UpdatePasswordDTO;
import com.example.blog.dto.auth.UpdateProfileDTO;
import com.example.blog.service.AuthService;
import com.example.blog.vo.LoginUserVO;
import com.example.blog.vo.LoginVO;
import com.example.blog.vo.UserProfileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "后台-认证管理")
@RestController
@RequestMapping("/admin/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    @OperationLog(module = "认证管理", operation = "管理员登录")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前登录信息")
    public Result<LoginUserVO> info(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(authService.getLoginUser(extractToken(authorization)));
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    @OperationLog(module = "认证管理", operation = "退出登录")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.logout(extractToken(authorization));
        return Result.success();
    }

    @GetMapping("/profile")
    @Operation(summary = "获取个人资料")
    public Result<UserProfileVO> profile() {
        return Result.success(authService.getCurrentUserProfile());
    }

    @PutMapping("/profile")
    @Operation(summary = "修改个人资料")
    @OperationLog(module = "认证管理", operation = "修改个人资料")
    public Result<Void> updateProfile(@Valid @RequestBody UpdateProfileDTO updateProfileDTO) {
        authService.updateCurrentUserProfile(updateProfileDTO);
        return Result.success();
    }

    @PutMapping("/password")
    @Operation(summary = "修改登录密码")
    @OperationLog(module = "认证管理", operation = "修改登录密码")
    public Result<Void> updatePassword(@Valid @RequestBody UpdatePasswordDTO updatePasswordDTO) {
        authService.updatePassword(updatePasswordDTO);
        return Result.success();
    }

    private String extractToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return null;
        }
        if (authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return authorization;
    }
}
