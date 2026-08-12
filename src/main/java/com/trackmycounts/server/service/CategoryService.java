package com.trackmycounts.server.service;

import com.trackmycounts.server.dto.CategoryVO;

import java.util.List;
import java.util.Map;

/**
 * 分类服务。一级分类只读（种子数据），二级分类可增删改。
 */
public interface CategoryService {

    /** 获取全部分类，按 type 分组：{expense: [...], income: [...]} */
    Map<String, List<CategoryVO>> getAllCategories();

    /** 新增二级分类 */
    CategoryVO.SubVO addSubCategory(Long groupId, String name);

    /** 编辑二级分类名称 */
    void updateSubCategory(Long subId, String name);

    /** 删除二级分类 */
    void deleteSubCategory(Long subId);
}
