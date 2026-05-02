package com.example.blog.controller.admin;

import com.example.blog.common.result.Result;
import com.example.blog.service.DashboardService;
import com.example.blog.vo.DashboardStatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "后台-仪表盘")
@RestController
@RequestMapping("/admin/dashboard")
public class DashboardAdminController {

    private final DashboardService dashboardService;

    public DashboardAdminController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取后台统计数据")
    public Result<DashboardStatisticsVO> statistics() {
        return Result.success(dashboardService.getStatistics());
    }
}
