package com.trackmycounts.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trackmycounts.server.dto.RecordQueryDTO;
import com.trackmycounts.server.dto.RecordVO;
import com.trackmycounts.server.entity.Record;
import com.trackmycounts.server.exception.BusinessException;
import com.trackmycounts.server.mapper.CategoryGroupMapper;
import com.trackmycounts.server.mapper.CategorySubMapper;
import com.trackmycounts.server.mapper.RecordMapper;
import com.trackmycounts.server.service.RecordService;
import com.trackmycounts.server.util.RecordVoHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final RecordMapper recordMapper;
    private final CategoryGroupMapper groupMapper;
    private final CategorySubMapper subMapper;

    @Override
    public Page<RecordVO> queryRecords(RecordQueryDTO query) {
        LambdaQueryWrapper<Record> wrapper = new LambdaQueryWrapper<>();

        // 类型筛选
        if (query.getType() != null && !"all".equals(query.getType())) {
            wrapper.eq(Record::getType, query.getType());
        }
        // 一级分类筛选
        if (query.getCat1Id() != null) {
            wrapper.eq(Record::getCat1Id, query.getCat1Id());
        }
        // 日期范围／快捷时间筛选
        LocalDate now = LocalDate.now();
        if (query.getStartDate() != null) {
            wrapper.ge(Record::getRecordDate, query.getStartDate());
        }
        if (query.getEndDate() != null) {
            wrapper.le(Record::getRecordDate, query.getEndDate());
        }
        // 快捷时间：用日期区间，避免 H2 不支持 DATE_FORMAT
        if (query.getTimeRange() != null && query.getStartDate() == null) {
            switch (query.getTimeRange()) {
                case "today" -> wrapper.eq(Record::getRecordDate, now);
                case "week" -> wrapper.ge(Record::getRecordDate, now.minusDays(6));
                case "month" -> {
                    LocalDate monthStart = now.withDayOfMonth(1);
                    wrapper.ge(Record::getRecordDate, monthStart)
                            .lt(Record::getRecordDate, monthStart.plusMonths(1));
                }
                case "year" -> {
                    LocalDate yearStart = now.withDayOfYear(1);
                    wrapper.ge(Record::getRecordDate, yearStart)
                            .lt(Record::getRecordDate, yearStart.plusYears(1));
                }
            }
        }
        // 关键词
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.like(Record::getNote, query.getKeyword());
        }
        // 排序
        wrapper.orderByDesc(Record::getRecordDate).orderByDesc(Record::getRecordTime);

        Page<Record> page = new Page<>(query.getPage(), query.getSize());
        Page<Record> result = recordMapper.selectPage(page, wrapper);

        // 批量转换为 VO（3 条 SQL 解决 N+1）
        Page<RecordVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(RecordVoHelper.toVOList(result.getRecords(), groupMapper, subMapper));
        return voPage;
    }

    @Override
    public RecordVO getRecordById(Long id) {
        Record record = recordMapper.selectById(id);
        if (record == null) return null;
        return RecordVoHelper.toVOList(List.of(record), groupMapper, subMapper).get(0);
    }

    @Override
    @Transactional
    public RecordVO addRecord(RecordVO vo) {
        Record record = new Record();
        record.setType(vo.getType());
        record.setAmount(vo.getAmount());
        record.setCat1Id(vo.getCat1Id());
        record.setCat2Id(vo.getCat2Id());
        record.setRecordDate(vo.getRecordDate());
        record.setRecordTime(vo.getRecordTime());
        record.setNote(vo.getNote() != null ? vo.getNote() : "");
        recordMapper.insert(record);
        return RecordVoHelper.toVOList(List.of(record), groupMapper, subMapper).get(0);
    }

    @Override
    @Transactional
    public RecordVO updateRecord(Long id, RecordVO vo) {
        Record record = recordMapper.selectById(id);
        if (record == null) throw new BusinessException("记录不存在");
        record.setType(vo.getType());
        record.setAmount(vo.getAmount());
        record.setCat1Id(vo.getCat1Id());
        record.setCat2Id(vo.getCat2Id());
        record.setRecordDate(vo.getRecordDate());
        record.setRecordTime(vo.getRecordTime());
        record.setNote(vo.getNote() != null ? vo.getNote() : "");
        recordMapper.updateById(record);
        return RecordVoHelper.toVOList(List.of(record), groupMapper, subMapper).get(0);
    }

    @Override
    public void deleteRecord(Long id) {
        recordMapper.deleteById(id);
    }

    @Override
    public void batchDelete(List<Long> ids) {
        recordMapper.deleteBatchIds(ids);
    }
}
