package com.company.userservice.infrastructure.persistence.nativeq.mapper;

import com.company.userservice.application.dto.report.UserReportRow;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class UserReportRowMapper implements RowMapper<UserReportRow> {
    @Override
    public UserReportRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserReportRow(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("status")
        );
    }
}
