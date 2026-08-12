package com.trackmycounts.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("category_group")
public class CategoryGroup {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String icon;
    private String color;
    private String type;      // expense / income
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
