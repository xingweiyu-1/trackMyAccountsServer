package com.trackmycounts.server.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class RecordQueryDTO {
    private String type;           // income / expense / all
    private Long cat1Id;           // 一级分类 ID
    private String keyword;        // 备注关键词
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String timeRange;      // today / week / month / year / all
    @Min(value = 1, message = "页码从1开始")
    private Integer page = 1;
    @Min(value = 1, message = "每页至少1条")
    private Integer size = 50;
}
