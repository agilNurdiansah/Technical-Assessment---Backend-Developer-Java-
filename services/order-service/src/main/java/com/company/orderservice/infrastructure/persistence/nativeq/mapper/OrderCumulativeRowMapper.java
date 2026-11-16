package com.company.orderservice.infrastructure.persistence.nativeq.mapper;

import com.company.orderservice.application.dto.report.OrderCumulativeReportRow;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.springframework.jdbc.core.RowMapper;

public class OrderCumulativeRowMapper implements RowMapper<OrderCumulativeReportRow> {
    @Override
    public OrderCumulativeReportRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderCumulativeReportRow row = new OrderCumulativeReportRow();
        row.setOrderId(rs.getLong("order_id"));
        row.setUserId(rs.getLong("user_id"));
        row.setStatus(rs.getString("status"));
        row.setTotalAmount(rs.getBigDecimal("total_amount"));
        Timestamp ts = rs.getTimestamp("created_at");
        row.setCreatedAt(ts != null ? ts.toInstant() : null);
        row.setOrderSequence(rs.getLong("order_sequence"));
        row.setCumulativeSpent(rs.getBigDecimal("cumulative_spent"));
        return row;
    }
}
