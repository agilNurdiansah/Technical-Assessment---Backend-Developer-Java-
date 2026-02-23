package com.company.orderservice.infrastructure.persistence.nativeq;

import com.company.orderservice.application.dto.report.OrderReportRow;
import com.company.orderservice.application.port.out.OrderReportRepository;
import com.company.orderservice.infrastructure.persistence.nativeq.mapper.OrderReportRowMapper;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderReportJdbcRepository implements OrderReportRepository {
    private final JdbcTemplate jdbcTemplate;

    public OrderReportJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<OrderReportRow> findOrderSummaryByUser() {
        String sql = "SELECT user_id, COUNT(*) AS order_count, COALESCE(SUM(total_amount), 0) AS total_amount "
            + "FROM orders GROUP BY user_id ORDER BY user_id";
        return jdbcTemplate.query(sql, new OrderReportRowMapper());
    }
}
