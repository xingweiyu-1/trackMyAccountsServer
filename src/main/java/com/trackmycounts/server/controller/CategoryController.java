package com.trackmycounts.server.controller;

import com.trackmycounts.server.common.Result;
import com.trackmycounts.server.dto.CategoryVO;
import com.trackmycounts.server.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 分类接口。
 * 产品策略：一级分类由系统种子数据固定提供，仅开放二级分类的增删改。
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /** 获取所有分类（按 type 分组） */
    @PostMapping
    public Result<Map<String, List<CategoryVO>>> getAll() {
        return Result.ok(categoryService.getAllCategories());
    }

    /** 新增二级分类 */
    @PostMapping("/sub")
    public Result<CategoryVO.SubVO> addSub(@RequestBody Map<String, Object> body) {
        if (body.get("groupId") == null) {
            return Result.fail("请选择一级分类");
        }
        Long groupId = Long.valueOf(body.get("groupId").toString());
        String name = (String) body.get("name");
        if (name == null || name.isBlank()) {
            return Result.fail("分类名称不能为空");
        }
        return Result.ok(categoryService.addSubCategory(groupId, name.trim()));
    }

    /** 编辑二级分类名称 */
    @PutMapping("/sub/{id}")
    public Result<Void> updateSub(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String name = body.get("name");
        if (name == null || name.isBlank()) {
            return Result.fail("分类名称不能为空");
        }
        categoryService.updateSubCategory(id, name.trim());
        return Result.ok();
    }

    /** 删除二级分类 */
    @DeleteMapping("/sub/{id}")
    public Result<Void> deleteSub(@PathVariable Long id) {
        categoryService.deleteSubCategory(id);
        return Result.ok();
    }
}
