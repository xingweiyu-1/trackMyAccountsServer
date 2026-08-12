package com.trackmycounts.server.service;

import com.trackmycounts.server.dto.*;

import java.util.List;

public interface AnalyticsService {

    /** 总收支概览 */
    AnalyticsOverviewVO getOverview();

    /** 月度趋势（近N个月） */
    List<TrendVO> getMonthlyTrend(int months);

    /** 分类占比 */
    List<CategoryBreakdownVO> getCategoryBreakdown(String type);

    /** 排行 Top N */
    List<RankingVO> getRanking(String type, int limit);

    /** 日均统计 */
    AvgStatsVO getAverages();
}
