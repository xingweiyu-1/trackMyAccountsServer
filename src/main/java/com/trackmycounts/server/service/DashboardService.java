package com.trackmycounts.server.service;

import com.trackmycounts.server.dto.*;

import java.util.List;

public interface DashboardService {

    /** 月度统计（收入/支出/结余） */
    DashboardStatsVO getMonthStats(String month);

    /** 最近 N 条交易 */
    List<RecordVO> getRecent(int limit);

    /** 月度支出分类占比 */
    List<CategoryBreakdownVO> getExpensePie(String month);

    /** 月度趋势（按天聚合） */
    List<TrendVO> getTrend(String month);
}
