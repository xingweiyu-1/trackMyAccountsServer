package com.trackmycounts.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsVO {
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal balance;
}
