package com.trackmycounts.server.dto;

import lombok.Data;

import java.util.List;

@Data
public class CategoryVO {
    private Long id;
    private String name;
    private String icon;
    private String color;
    private String type;
    private List<SubVO> subs;

    @Data
    public static class SubVO {
        private Long id;
        private String name;
    }
}
