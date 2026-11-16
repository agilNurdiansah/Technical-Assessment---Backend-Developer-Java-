package com.company.orderservice.infrastructure.persistence.nativeq.mapper;

import com.company.orderservice.application.dto.report.UserRankingReportRow;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class UserRankingRowMapper implements RowMapper<UserRankingReportRow> {
    @Override
    public UserRankingReportRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserRankingReportRow row = new UserRankingReportRow();
        row.setUserId(rs.getLong("user_id"));
        row.setTotalOrders(rs.getLong("total_orders"));
        row.setTotalSpent(rs.getBigDecimal("total_spent"));
        row.setAvgOrderAmount(rs.getBigDecimal("avg_order_amount"));
        row.setSpendingRank(rs.getLong("spending_rank"));
        return row;
    }
}
