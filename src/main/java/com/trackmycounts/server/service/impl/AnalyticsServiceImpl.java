package com.trackmycounts.server.service.impl;

import com.trackmycounts.server.dto.*;
import com.trackmycounts.server.mapper.RecordMapper;
import com.trackmycounts.server.service.AnalyticsService;
import com.trackmycounts.server.util.MapValueHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final RecordMapper recordMapper;

    @Override
    public AnalyticsOverviewVO getOverview() {
        Map<String, Object> summary = recordMapper.selectTotalSummary();
        BigDecimal income = MapValueHelper.getBigDecimal(summary, "totalIncome");
        BigDecimal expense = MapValueHelper.getBigDecimal(summary, "totalExpense");
        return new AnalyticsOverviewVO(income, expense, income.subtract(expense));
    }

    @Override
    public List<TrendVO> getMonthlyTrend(int months) {
        LocalDate startDate = LocalDate.now().minusMonths(Math.max(months, 1));
        List<TrendVO> list = recordMapper.selectMonthlyTrend(startDate);
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public List<CategoryBreakdownVO> getCategoryBreakdown(String type) {
        List<CategoryBreakdownVO> list = recordMapper.selectCategoryBreakdown(type);
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public List<RankingVO> getRanking(String type, int limit) {
        List<RankingVO> list = recordMapper.selectRanking(type, Math.max(1, limit));
        if (list == null) {
            return Collections.emptyList();
        }
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRank(i + 1);
        }
        return list;
    }

    @Override
    public AvgStatsVO getAverages() {
        Map<String, Object> avgRow = recordMapper.selectDailyAveragesMap();
        AvgStatsVO vo = new AvgStatsVO();
        vo.setDailyAvgExpense(MapValueHelper.getBigDecimal(avgRow, "dailyAvgExpense"));
        vo.setDailyAvgIncome(MapValueHelper.getBigDecimal(avgRow, "dailyAvgIncome"));

        List<Map<String, Object>> maxExp = recordMapper.selectMaxAmountExpense();
        if (maxExp != null && !maxExp.isEmpty()) {
            Map<String, Object> row = maxExp.get(0);
            vo.setMaxDailyExpense(MapValueHelper.getBigDecimal(row, "amount"));
            vo.setMaxDailyExpenseDate(MapValueHelper.getString(row, "record_date"));
            vo.setMaxDailyExpenseCat(MapValueHelper.getString(row, "catName"));
        }

        List<Map<String, Object>> maxInc = recordMapper.selectMaxAmountIncome();
        if (maxInc != null && !maxInc.isEmpty()) {
            Map<String, Object> row = maxInc.get(0);
            vo.setMaxDailyIncome(MapValueHelper.getBigDecimal(row, "amount"));
            vo.setMaxDailyIncomeDate(MapValueHelper.getString(row, "record_date"));
            vo.setMaxDailyIncomeCat(MapValueHelper.getString(row, "catName"));
        }

        return vo;
    }
}
