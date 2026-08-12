package com.trackmycounts.server.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * H2 / MySQL：种子数据或历史插入后，校准自增主键，避免再插入时撞 PRIMARY KEY。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SequenceResetRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        reset("category_group");
        reset("category_sub");
        reset("t_record");
    }

    private void reset(String table) {
        try {
            Long maxId = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(MAX(id), 0) FROM " + table, Long.class);
            long next = (maxId == null ? 0L : maxId) + 1;
            // H2
            try {
                jdbcTemplate.execute(
                        "ALTER TABLE " + table + " ALTER COLUMN id RESTART WITH " + next);
                log.info("已校准 {} 自增起点为 {}", table, next);
                return;
            } catch (Exception ignored) {
                // fall through to MySQL
            }
            // MySQL
            jdbcTemplate.execute("ALTER TABLE " + table + " AUTO_INCREMENT = " + next);
            log.info("已校准 {} AUTO_INCREMENT 为 {}", table, next);
        } catch (Exception e) {
            log.warn("校准 {} 自增失败（可忽略）: {}", table, e.getMessage());
        }
    }
}
