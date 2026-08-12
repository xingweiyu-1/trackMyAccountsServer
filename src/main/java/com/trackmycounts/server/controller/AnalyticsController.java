package com.trackmycounts.server.controller;

import com.trackmycounts.server.common.Result;
import com.trackmycounts.server.dto.*;
import com.trackmycounts.server.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /** 总收支概览 */
    @PostMapping("/overview")
    public Result<AnalyticsOverviewVO> overview() {
        return Result.ok(analyticsService.getOverview());
    }

    /** 月度趋势（柱状图） */
    @PostMapping("/monthly-trend")
    public Result<List<TrendVO>> monthlyTrend(@RequestBody(required = false) Map<String, Object> body) {
        int months = body != null && body.get("months") != null
                ? Integer.parseInt(body.get("months").toString()) : 4;
        return Result.ok(analyticsService.getMonthlyTrend(months));
    }

    /** 分类占比 */
    @PostMapping("/category-breakdown")
    public Result<List<CategoryBreakdownVO>> categoryBreakdown(@RequestBody(required = false) Map<String, Object> body) {
        String type = body != null && body.get("type") != null
                ? body.get("type").toString() : "expense";
        return Result.ok(analyticsService.getCategoryBreakdown(type));
    }

    /** 分类排行 Top N */
    @PostMapping("/ranking")
    public Result<List<RankingVO>> ranking(@RequestBody(required = false) Map<String, Object> body) {
        String type = body != null && body.get("type") != null
                ? body.get("type").toString() : "expense";
        int limit = body != null && body.get("limit") != null
                ? Integer.parseInt(body.get("limit").toString()) : 10;
        return Result.ok(analyticsService.getRanking(type, limit));
    }

    /** 日均统计 + 最高单日 */
    @PostMapping("/averages")
    public Result<AvgStatsVO> averages() {
        return Result.ok(analyticsService.getAverages());
    }
}
