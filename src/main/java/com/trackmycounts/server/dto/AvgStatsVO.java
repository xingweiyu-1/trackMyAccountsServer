package com.trackmycounts.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvgStatsVO {
    private BigDecimal dailyAvgExpense;
    private BigDecimal dailyAvgIncome;
    private BigDecimal maxDailyExpense;
    private String maxDailyExpenseDate;
    private String maxDailyExpenseCat;
    private BigDecimal maxDailyIncome;
    private String maxDailyIncomeDate;
    private String maxDailyIncomeCat;
}
