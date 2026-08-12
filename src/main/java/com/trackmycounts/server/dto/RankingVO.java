package com.trackmycounts.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankingVO {
    private Integer rank;
    private String name;
    private String icon;
    private String color;
    private BigDecimal amount;
    private BigDecimal percentage;
}
