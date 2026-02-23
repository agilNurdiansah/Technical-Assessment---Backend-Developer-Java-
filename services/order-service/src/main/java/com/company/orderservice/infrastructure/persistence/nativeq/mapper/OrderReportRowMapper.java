package com.company.orderservice.infrastructure.persistence.nativeq.mapper;

import com.company.orderservice.application.dto.report.OrderReportRow;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class OrderReportRowMapper implements RowMapper<OrderReportRow> {
    @Override
    public OrderReportRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new OrderReportRow(
            rs.getLong("user_id"),
            rs.getLong("order_count"),
            rs.getBigDecimal("total_amount")
        );
    }
}
