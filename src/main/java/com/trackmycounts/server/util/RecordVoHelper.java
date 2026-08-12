package com.trackmycounts.server.util;

import com.trackmycounts.server.dto.RecordVO;
import com.trackmycounts.server.entity.CategoryGroup;
import com.trackmycounts.server.entity.CategorySub;
import com.trackmycounts.server.entity.Record;
import com.trackmycounts.server.mapper.CategoryGroupMapper;
import com.trackmycounts.server.mapper.CategorySubMapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Record → RecordVO 转换工具，批量解决 N+1 查询问题
 */
public final class RecordVoHelper {

    private RecordVoHelper() {}

    /**
     * 批量转换，只执行 3 次 SQL（1 次记录查询 + 1 次一级分类 + 1 次二级分类）
     */
    public static List<RecordVO> toVOList(List<Record> records,
                                           CategoryGroupMapper groupMapper,
                                           CategorySubMapper subMapper) {
        if (records == null || records.isEmpty()) return Collections.emptyList();

        // 收集分类 ID
        Set<Long> cat1Ids = new HashSet<>();
        Set<Long> cat2Ids = new HashSet<>();
        for (Record r : records) {
            if (r.getCat1Id() != null) cat1Ids.add(r.getCat1Id());
            if (r.getCat2Id() != null) cat2Ids.add(r.getCat2Id());
        }

        // 批量查询
        Map<Long, CategoryGroup> groupMap = cat1Ids.isEmpty() ? Collections.emptyMap() :
                groupMapper.selectBatchIds(cat1Ids).stream()
                        .collect(Collectors.toMap(CategoryGroup::getId, Function.identity()));
        Map<Long, CategorySub> subMap = cat2Ids.isEmpty() ? Collections.emptyMap() :
                subMapper.selectBatchIds(cat2Ids).stream()
                        .collect(Collectors.toMap(CategorySub::getId, Function.identity()));

        // 组装
        return records.stream().map(r -> toVO(r, groupMap, subMap)).toList();
    }

    /** 单条转换（复用批量缓存） */
    private static RecordVO toVO(Record r,
                                  Map<Long, CategoryGroup> groupMap,
                                  Map<Long, CategorySub> subMap) {
        RecordVO vo = new RecordVO();
        vo.setId(r.getId());
        vo.setType(r.getType());
        vo.setAmount(r.getAmount());
        vo.setCat1Id(r.getCat1Id());
        vo.setCat2Id(r.getCat2Id());
        vo.setRecordDate(r.getRecordDate());
        vo.setRecordTime(r.getRecordTime());
        vo.setNote(r.getNote());

        CategoryGroup g = groupMap.get(r.getCat1Id());
        if (g != null) vo.setCat1(g.getName());
        CategorySub s = subMap.get(r.getCat2Id());
        if (s != null) vo.setCat2(s.getName());

        return vo;
    }
}
