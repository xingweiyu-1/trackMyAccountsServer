package com.trackmycounts.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trackmycounts.server.dto.CategoryVO;
import com.trackmycounts.server.entity.CategoryGroup;
import com.trackmycounts.server.entity.CategorySub;
import com.trackmycounts.server.exception.BusinessException;
import com.trackmycounts.server.mapper.CategoryGroupMapper;
import com.trackmycounts.server.mapper.CategorySubMapper;
import com.trackmycounts.server.mapper.RecordMapper;
import com.trackmycounts.server.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryGroupMapper groupMapper;
    private final CategorySubMapper subMapper;
    private final RecordMapper recordMapper;

    @Override
    public Map<String, List<CategoryVO>> getAllCategories() {
        List<CategoryGroup> groups = groupMapper.selectList(
                new LambdaQueryWrapper<CategoryGroup>().orderByAsc(CategoryGroup::getSortOrder));

        Map<String, List<CategoryGroup>> typeMap = groups.stream()
                .collect(Collectors.groupingBy(CategoryGroup::getType));

        List<CategorySub> allSubs = subMapper.selectList(
                new LambdaQueryWrapper<CategorySub>().orderByAsc(CategorySub::getSortOrder));
        Map<Long, List<CategorySub>> subMap = allSubs.stream()
                .collect(Collectors.groupingBy(CategorySub::getGroupId));

        Map<String, List<CategoryVO>> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<CategoryGroup>> entry : typeMap.entrySet()) {
            List<CategoryVO> list = entry.getValue().stream().map(g -> {
                CategoryVO vo = new CategoryVO();
                vo.setId(g.getId());
                vo.setName(g.getName());
                vo.setIcon(g.getIcon());
                vo.setColor(g.getColor());
                vo.setType(g.getType());
                vo.setSubs(subMap.getOrDefault(g.getId(), Collections.emptyList()).stream().map(s -> {
                    CategoryVO.SubVO subVO = new CategoryVO.SubVO();
                    subVO.setId(s.getId());
                    subVO.setName(s.getName());
                    return subVO;
                }).collect(Collectors.toList()));
                return vo;
            }).collect(Collectors.toList());
            result.put(entry.getKey(), list);
        }

        result.putIfAbsent("expense", Collections.emptyList());
        result.putIfAbsent("income", Collections.emptyList());

        return result;
    }

    @Override
    @Transactional
    public CategoryVO.SubVO addSubCategory(Long groupId, String name) {
        CategoryGroup group = groupMapper.selectById(groupId);
        if (group == null) {
            throw new BusinessException("一级分类不存在");
        }
        Long exists = subMapper.selectCount(new LambdaQueryWrapper<CategorySub>()
                .eq(CategorySub::getGroupId, groupId)
                .eq(CategorySub::getName, name));
        if (exists != null && exists > 0) {
            throw new BusinessException("该一级分类下已存在同名二级分类");
        }

        CategorySub sub = new CategorySub();
        sub.setGroupId(groupId);
        sub.setName(name);
        sub.setSortOrder(99);
        subMapper.insert(sub);

        CategoryVO.SubVO vo = new CategoryVO.SubVO();
        vo.setId(sub.getId());
        vo.setName(sub.getName());
        return vo;
    }

    @Override
    public void updateSubCategory(Long subId, String name) {
        CategorySub existing = subMapper.selectById(subId);
        if (existing == null) {
            throw new BusinessException("二级分类不存在");
        }
        Long dup = subMapper.selectCount(new LambdaQueryWrapper<CategorySub>()
                .eq(CategorySub::getGroupId, existing.getGroupId())
                .eq(CategorySub::getName, name)
                .ne(CategorySub::getId, subId));
        if (dup != null && dup > 0) {
            throw new BusinessException("该一级分类下已存在同名二级分类");
        }
        existing.setName(name);
        subMapper.updateById(existing);
    }

    @Override
    public void deleteSubCategory(Long subId) {
        int refCount = recordMapper.countByCat2Id(subId);
        if (refCount > 0) {
            throw new BusinessException("该分类下有 " + refCount + " 条记录，不可删除");
        }
        subMapper.deleteById(subId);
    }
}
