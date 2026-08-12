package com.trackmycounts.server.controller;

import com.trackmycounts.server.common.Result;
import com.trackmycounts.server.dto.*;
import com.trackmycounts.server.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /** 月度收支统计 */
    @PostMapping("/stats")
    public Result<DashboardStatsVO> stats(@RequestBody(required = false) Map<String, Object> body) {
        String month = body != null && body.get("month") != null
                ? body.get("month").toString() : "";
        return Result.ok(dashboardService.getMonthStats(month));
    }

    /** 最近 N 条交易 */
    @PostMapping("/recent")
    public Result<List<RecordVO>> recent(@RequestBody(required = false) Map<String, Object> body) {
        int limit = body != null && body.get("limit") != null
                ? Integer.parseInt(body.get("limit").toString()) : 5;
        return Result.ok(dashboardService.getRecent(limit));
    }

    /** 支出分类占比（饼图） */
    @PostMapping("/expense-pie")
    public Result<List<CategoryBreakdownVO>> expensePie(@RequestBody(required = false) Map<String, Object> body) {
        String month = body != null && body.get("month") != null
                ? body.get("month").toString() : "";
        return Result.ok(dashboardService.getExpensePie(month));
    }

    /** 月度趋势（折线图） */
    @PostMapping("/trend")
    public Result<List<TrendVO>> trend(@RequestBody(required = false) Map<String, Object> body) {
        String month = body != null && body.get("month") != null
                ? body.get("month").toString() : "";
        return Result.ok(dashboardService.getTrend(month));
    }
}
