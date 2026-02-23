package com.company.userservice.infrastructure.persistence.nativeq;

import com.company.userservice.application.dto.report.UserReportRow;
import com.company.userservice.application.port.out.UserReportRepository;
import com.company.userservice.infrastructure.persistence.nativeq.mapper.UserReportRowMapper;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserReportJdbcRepository implements UserReportRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserReportJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<UserReportRow> findActiveUsers() {
        String sql = "SELECT id, name, email, status FROM users WHERE status = 'ACTIVE' ORDER BY id";
        return jdbcTemplate.query(sql, new UserReportRowMapper());
    }
}
