package com.trackmycounts.server.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class RecordVO {
    private Long id;
    @NotBlank(message = "收支类型不能为空")
    private String type;
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;
    private String cat1;
    private String cat2;
    @NotNull(message = "请选择一级分类")
    private Long cat1Id;
    @NotNull(message = "请选择二级分类")
    private Long cat2Id;
    @NotNull(message = "请选择日期")
    private LocalDate recordDate;
    private LocalTime recordTime;
    private String note;
}
