package com.trackmycounts.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trackmycounts.server.dto.*;
import com.trackmycounts.server.entity.Record;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 记账记录 Mapper。
 * 日期过滤在 Java 侧算好区间，SQL 只用 >= / <，兼容 H2 / MySQL。
 */
@Mapper
public interface RecordMapper extends BaseMapper<Record> {

    /** 按类型汇总总金额 */
    @Select("""
        SELECT
            COALESCE(SUM(CASE WHEN type='income' THEN amount ELSE 0 END), 0) AS totalIncome,
            COALESCE(SUM(CASE WHEN type='expense' THEN amount ELSE 0 END), 0) AS totalExpense
        FROM t_record
        """)
    Map<String, Object> selectTotalSummary();

    /** 按日期区间汇总 [start, end) */
    @Select("""
        SELECT
            COALESCE(SUM(CASE WHEN type='income' THEN amount ELSE 0 END), 0) AS totalIncome,
            COALESCE(SUM(CASE WHEN type='expense' THEN amount ELSE 0 END), 0) AS totalExpense
        FROM t_record
        WHERE record_date >= #{startDate} AND record_date < #{endDate}
        """)
    Map<String, Object> selectSummaryByDateRange(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    /** 区间内按天趋势 */
    @Select("""
        SELECT
            CONCAT(CAST(EXTRACT(DAY FROM record_date) AS CHAR), '日') AS label,
            COALESCE(SUM(CASE WHEN type='income' THEN amount ELSE 0 END), 0) AS income,
            COALESCE(SUM(CASE WHEN type='expense' THEN amount ELSE 0 END), 0) AS expense
        FROM t_record
        WHERE record_date >= #{startDate} AND record_date < #{endDate}
        GROUP BY record_date
        ORDER BY record_date
        """)
    List<TrendVO> selectTrendByDateRange(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    /** 区间内支出分类占比 */
    @Results(id = "categoryBreakdownMap", value = {
            @Result(column = "name", property = "name"),
            @Result(column = "amount_sum", property = "value"),
            @Result(column = "color", property = "color")
    })
    @Select("""
        SELECT cg.name, COALESCE(SUM(r.amount), 0) AS amount_sum, cg.color
        FROM t_record r
        JOIN category_group cg ON cg.id = r.cat1_id
        WHERE r.type = 'expense'
          AND r.record_date >= #{startDate} AND r.record_date < #{endDate}
        GROUP BY cg.id, cg.name, cg.color
        ORDER BY amount_sum DESC
        """)
    List<CategoryBreakdownVO> selectExpensePieByDateRange(@Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);

    /**
     * 近 N 个月趋势（startDate 由 Service 计算）。
     * 先按年月聚合再拼 label，避免 H2 对 SELECT 中 CONCAT(record_date…) 要求进 GROUP BY。
     */
    @Select("""
        SELECT
            CONCAT(CAST(ym AS CHAR), '月') AS label,
            COALESCE(SUM(income), 0) AS income,
            COALESCE(SUM(expense), 0) AS expense
        FROM (
            SELECT
                EXTRACT(YEAR FROM record_date) AS yr,
                EXTRACT(MONTH FROM record_date) AS ym,
                CASE WHEN type='income' THEN amount ELSE 0 END AS income,
                CASE WHEN type='expense' THEN amount ELSE 0 END AS expense
            FROM t_record
            WHERE record_date >= #{startDate}
        ) t
        GROUP BY yr, ym
        ORDER BY yr, ym
        """)
    List<TrendVO> selectMonthlyTrend(@Param("startDate") LocalDate startDate);

    /** 分类占比 */
    @ResultMap("categoryBreakdownMap")
    @Select("""
        SELECT cg.name, COALESCE(SUM(r.amount), 0) AS amount_sum, cg.color
        FROM t_record r
        JOIN category_group cg ON cg.id = r.cat1_id
        WHERE r.type = #{type}
        GROUP BY cg.id, cg.name, cg.color
        ORDER BY amount_sum DESC
        """)
    List<CategoryBreakdownVO> selectCategoryBreakdown(@Param("type") String type);

    /** 分类排行 Top N */
    @Select("""
        SELECT cg.name, cg.icon, cg.color,
               COALESCE(SUM(r.amount), 0) AS amount,
               ROUND(COALESCE(SUM(r.amount), 0) / NULLIF((
                   SELECT COALESCE(SUM(amount), 0) FROM t_record WHERE type = #{type}
               ), 0) * 100, 1) AS percentage
        FROM t_record r
        JOIN category_group cg ON cg.id = r.cat1_id
        WHERE r.type = #{type}
        GROUP BY cg.id, cg.name, cg.icon, cg.color
        ORDER BY amount DESC
        LIMIT #{limit}
        """)
    List<RankingVO> selectRanking(@Param("type") String type, @Param("limit") int limit);

    /** 日均：返回 Map，避免 H2 列名大小写导致 VO 映射失败 */
    @Select("""
        SELECT
            COALESCE(ROUND(SUM(CASE WHEN type='expense' THEN amount ELSE 0 END) /
                NULLIF(COUNT(DISTINCT record_date), 0), 2), 0) AS dailyAvgExpense,
            COALESCE(ROUND(SUM(CASE WHEN type='income' THEN amount ELSE 0 END) /
                NULLIF(COUNT(DISTINCT record_date), 0), 2), 0) AS dailyAvgIncome
        FROM t_record
        """)
    Map<String, Object> selectDailyAveragesMap();

    @Select("SELECT COUNT(1) FROM t_record WHERE cat2_id = #{subId}")
    int countByCat2Id(@Param("subId") Long subId);

    @Select("""
        SELECT amount, record_date, cg.name AS catName
        FROM t_record r
        JOIN category_group cg ON cg.id = r.cat1_id
        WHERE r.type = 'expense'
        ORDER BY amount DESC
        LIMIT 1
        """)
    List<Map<String, Object>> selectMaxAmountExpense();

    @Select("""
        SELECT amount, record_date, cg.name AS catName
        FROM t_record r
        JOIN category_group cg ON cg.id = r.cat1_id
        WHERE r.type = 'income'
        ORDER BY amount DESC
        LIMIT 1
        """)
    List<Map<String, Object>> selectMaxAmountIncome();
}
