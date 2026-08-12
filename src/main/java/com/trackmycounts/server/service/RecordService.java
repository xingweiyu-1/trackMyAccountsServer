package com.trackmycounts.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trackmycounts.server.dto.RecordQueryDTO;
import com.trackmycounts.server.dto.RecordVO;

import java.util.List;

public interface RecordService {

    /** 分页/条件查询记录 */
    Page<RecordVO> queryRecords(RecordQueryDTO query);

    /** 获取单条记录 */
    RecordVO getRecordById(Long id);

    /** 新增记录 */
    RecordVO addRecord(RecordVO vo);

    /** 编辑记录 */
    RecordVO updateRecord(Long id, RecordVO vo);

    /** 删除单条 */
    void deleteRecord(Long id);

    /** 批量删除 */
    void batchDelete(List<Long> ids);
}
