package com.example.blog.controller.portal;

import com.example.blog.common.result.Result;
import com.example.blog.service.SiteConfigService;
import com.example.blog.vo.SiteConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "前台-站点信息")
@RestController
@RequestMapping("/site")
public class SiteController {

    private final SiteConfigService siteConfigService;

    public SiteController(SiteConfigService siteConfigService) {
        this.siteConfigService = siteConfigService;
    }

    @GetMapping("/info")
    @Operation(summary = "获取站点信息")
    public Result<SiteConfigVO> info() {
        return Result.success(siteConfigService.getCurrent());
    }
}
