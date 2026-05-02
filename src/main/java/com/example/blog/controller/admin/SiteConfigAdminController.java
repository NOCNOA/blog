package com.example.blog.controller.admin;

import com.example.blog.annotation.OperationLog;
import com.example.blog.common.result.Result;
import com.example.blog.dto.site.SiteConfigDTO;
import com.example.blog.service.SiteConfigService;
import com.example.blog.vo.SiteConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "后台-站点配置")
@RestController
@RequestMapping("/admin/site")
public class SiteConfigAdminController {

    private final SiteConfigService siteConfigService;

    public SiteConfigAdminController(SiteConfigService siteConfigService) {
        this.siteConfigService = siteConfigService;
    }

    @GetMapping("/config")
    @Operation(summary = "获取站点配置")
    public Result<SiteConfigVO> getConfig() {
        return Result.success(siteConfigService.getCurrent());
    }

    @PutMapping("/config")
    @Operation(summary = "修改站点配置")
    @OperationLog(module = "站点配置", operation = "修改站点配置")
    public Result<Void> updateConfig(@Valid @RequestBody SiteConfigDTO siteConfigDTO) {
        siteConfigService.updateCurrent(siteConfigDTO);
        return Result.success();
    }
}
