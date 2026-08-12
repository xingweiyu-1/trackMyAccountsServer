package com.trackmycounts.server.controller;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.trackmycounts.server.common.Result;
import com.trackmycounts.server.entity.Record;
import com.trackmycounts.server.service.RecordService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataController {

    private final RecordService recordService;
    private final com.trackmycounts.server.mapper.RecordMapper recordMapper;

    /** CSV 导出 — 返回二进制流不走统一 Result，但仍通过 try-catch 保证错误可跟踪 */
    @PostMapping("/export/csv")
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv;charset=UTF-8");
        String filename = URLEncoder.encode("记账数据.csv", StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + filename);
        response.setCharacterEncoding("UTF-8");

        // 写入 BOM 防止 Excel 打开中文乱码
        response.getOutputStream().write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

        List<Record> records = recordMapper.selectList(null);
        try (CSVWriter writer = new CSVWriter(
                new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {
            writer.writeNext(new String[]{"ID", "类型", "金额", "一级分类ID", "二级分类ID", "日期", "时间", "备注"});
            for (Record r : records) {
                writer.writeNext(new String[]{
                        String.valueOf(r.getId()),
                        r.getType(),
                        r.getAmount().toString(),
                        String.valueOf(r.getCat1Id()),
                        String.valueOf(r.getCat2Id()),
                        r.getRecordDate() != null ? r.getRecordDate().toString() : "",
                        r.getRecordTime() != null ? r.getRecordTime().toString() : "",
                        r.getNote() != null ? r.getNote() : ""
                });
            }
        }
    }

    /** CSV 导入 — 使用 OpenCSV 的 CSVReader 正确处理含逗号/引号字段 */
    @PostMapping("/import/csv")
    public Result<Integer> importCsv(@RequestParam("file") MultipartFile file) throws IOException {
        int count = 0;
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> rows = reader.readAll();
            for (int i = 0; i < rows.size(); i++) {
                String[] cols = rows.get(i);
                if (i == 0) {
                    // 跳过表头（若首列为 "ID" 则确认为表头）
                    if (cols.length > 0 && cols[0].trim().equals("ID")) continue;
                }
                if (cols.length < 7) continue;

                Record record = new Record();
                record.setType(cols[1].trim());
                record.setAmount(new BigDecimal(cols[2].trim()));
                record.setCat1Id(Long.valueOf(cols[3].trim()));
                record.setCat2Id(Long.valueOf(cols[4].trim()));
                record.setRecordDate(LocalDate.parse(cols[5].trim()));
                record.setRecordTime(LocalTime.parse(cols[6].trim()));
                record.setNote(cols.length > 7 ? cols[7].trim() : "");
                recordMapper.insert(record);
                count++;
            }
        } catch (CsvException e) {
            return Result.fail("CSV 解析失败: " + e.getMessage());
        }
        return Result.ok(count);
    }

    /** 清空所有数据 */
    @DeleteMapping("/clear")
    public Result<Void> clearAll() {
        recordService.batchDelete(
                recordMapper.selectList(null).stream().map(com.trackmycounts.server.entity.Record::getId).toList());
        return Result.ok();
    }
}
