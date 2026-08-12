package com.trackmycounts.server.service.impl;

import com.trackmycounts.server.dto.*;
import com.trackmycounts.server.entity.Record;
import com.trackmycounts.server.mapper.CategoryGroupMapper;
import com.trackmycounts.server.mapper.CategorySubMapper;
import com.trackmycounts.server.mapper.RecordMapper;
import com.trackmycounts.server.service.DashboardService;
import com.trackmycounts.server.util.MapValueHelper;
import com.trackmycounts.server.util.RecordVoHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final RecordMapper recordMapper;
    private final CategoryGroupMapper groupMapper;
    private final CategorySubMapper subMapper;

    @Override
    public DashboardStatsVO getMonthStats(String month) {
        LocalDate[] range = monthRange(month);
        Map<String, Object> summary = range == null
                ? recordMapper.selectTotalSummary()
                : recordMapper.selectSummaryByDateRange(range[0], range[1]);
        BigDecimal income = MapValueHelper.getBigDecimal(summary, "totalIncome");
        BigDecimal expense = MapValueHelper.getBigDecimal(summary, "totalExpense");
        return new DashboardStatsVO(income, expense, income.subtract(expense));
    }

    @Override
    public List<RecordVO> getRecent(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        List<Record> records = recordMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Record>()
                        .orderByDesc(Record::getRecordDate)
                        .orderByDesc(Record::getRecordTime)
                        .last("LIMIT " + safeLimit));
        return RecordVoHelper.toVOList(records, groupMapper, subMapper);
    }

    @Override
    public List<CategoryBreakdownVO> getExpensePie(String month) {
        LocalDate[] range = monthRange(month);
        if (range == null) {
            return recordMapper.selectCategoryBreakdown("expense");
        }
        List<CategoryBreakdownVO> list = recordMapper.selectExpensePieByDateRange(range[0], range[1]);
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public List<TrendVO> getTrend(String month) {
        LocalDate[] range = monthRange(month);
        if (range == null) {
            return Collections.emptyList();
        }
        List<TrendVO> list = recordMapper.selectTrendByDateRange(range[0], range[1]);
        return list != null ? list : Collections.emptyList();
    }

    /** @return [startInclusive, endExclusive]，month 无效时返回 null 表示不按月过滤 */
    private static LocalDate[] monthRange(String month) {
        if (month == null || month.isBlank()) {
            return null;
        }
        try {
            YearMonth ym = YearMonth.parse(month.trim());
            LocalDate start = ym.atDay(1);
            return new LocalDate[]{start, start.plusMonths(1)};
        } catch (Exception e) {
            return null;
        }
    }
}
