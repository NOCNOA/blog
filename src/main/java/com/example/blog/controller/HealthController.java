package com.example.blog.controller;

import com.example.blog.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "系统-健康检查")
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    @Operation(summary = "检查服务是否正常运行")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", "UP");
        data.put("service", "blog");
        return Result.success(data);
    }
}
