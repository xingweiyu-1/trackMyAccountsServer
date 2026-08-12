package com.trackmycounts.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("t_record")
public class Record {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String type;          // income / expense
    private BigDecimal amount;
    private Long cat1Id;
    private Long cat2Id;
    private LocalDate recordDate;
    private LocalTime recordTime;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
