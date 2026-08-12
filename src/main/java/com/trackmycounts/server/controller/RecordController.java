package com.trackmycounts.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trackmycounts.server.common.Result;
import com.trackmycounts.server.dto.RecordQueryDTO;
import com.trackmycounts.server.dto.RecordVO;
import com.trackmycounts.server.service.RecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    /** 分页查询记录 */
    @PostMapping("/query")
    public Result<Page<RecordVO>> query(@Valid @RequestBody RecordQueryDTO query) {
        return Result.ok(recordService.queryRecords(query));
    }

    /** 获取单条记录 */
    @PostMapping("/{id}")
    public Result<RecordVO> getById(@PathVariable Long id) {
        RecordVO vo = recordService.getRecordById(id);
        return vo != null ? Result.ok(vo) : Result.fail("记录不存在");
    }

    /** 新增记录 */
    @PostMapping
    public Result<RecordVO> add(@Valid @RequestBody RecordVO vo) {
        if (vo.getAmount() == null || vo.getAmount().signum() <= 0) {
            return Result.fail("请输入有效金额");
        }
        if (vo.getCat1Id() == null || vo.getCat2Id() == null) {
            return Result.fail("请选择分类");
        }
        if (vo.getType() == null || vo.getType().isBlank()) {
            return Result.fail("请选择收支类型");
        }
        if (vo.getRecordDate() == null) {
            return Result.fail("请选择日期");
        }
        return Result.ok(recordService.addRecord(vo));
    }

    /** 编辑记录 */
    @PutMapping("/{id}")
    public Result<RecordVO> update(@PathVariable Long id, @Valid @RequestBody RecordVO vo) {
        return Result.ok(recordService.updateRecord(id, vo));
    }

    /** 删除单条记录 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return Result.ok();
    }

    /** 批量删除 */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.fail("请选择要删除的记录");
        }
        recordService.batchDelete(ids);
        return Result.ok();
    }
}
