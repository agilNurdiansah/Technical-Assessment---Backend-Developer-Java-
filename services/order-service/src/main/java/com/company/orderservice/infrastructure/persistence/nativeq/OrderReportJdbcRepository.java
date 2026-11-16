package com.company.orderservice.infrastructure.persistence.nativeq;

import com.company.orderservice.application.dto.report.OrderCumulativeReportRow;
import com.company.orderservice.application.dto.report.OrderReportRow;
import com.company.orderservice.application.dto.report.ProductPerformanceReportRow;
import com.company.orderservice.application.dto.report.UserRankingReportRow;
import com.company.orderservice.application.port.out.OrderReportRepository;
import com.company.orderservice.infrastructure.persistence.nativeq.mapper.OrderCumulativeRowMapper;
import com.company.orderservice.infrastructure.persistence.nativeq.mapper.OrderReportRowMapper;
import com.company.orderservice.infrastructure.persistence.nativeq.mapper.ProductPerformanceRowMapper;
import com.company.orderservice.infrastructure.persistence.nativeq.mapper.UserRankingRowMapper;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository showcasing Intermediate Native SQL Queries (Middle Level Requirement).
 * Features:
 * 1. CTE (Common Table Expressions WITH ... AS ...)
 * 2. Window Functions (DENSE_RANK, ROW_NUMBER, SUM() OVER (...))
 * 3. Multi-table JOINs with GROUP BY and HAVING filters
 */
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

    /**
     * Intermediate Query 1: CTE + Window Function (DENSE_RANK)
     * Groups customer orders and computes competitive rank based on spending.
     */
    @Override
    public List<UserRankingReportRow> findUserSpendingRankings() {
        String sql = "WITH UserOrderStats AS ("
            + "    SELECT "
            + "        o.user_id, "
            + "        COUNT(o.id) AS total_orders, "
            + "        COALESCE(SUM(o.total_amount), 0) AS total_spent, "
            + "        COALESCE(AVG(o.total_amount), 0) AS avg_order_amount "
            + "    FROM orders o "
            + "    WHERE o.status != 'CANCELLED' "
            + "    GROUP BY o.user_id "
            + ") "
            + "SELECT "
            + "    user_id, "
            + "    total_orders, "
            + "    total_spent, "
            + "    avg_order_amount, "
            + "    DENSE_RANK() OVER (ORDER BY total_spent DESC) AS spending_rank "
            + "FROM UserOrderStats "
            + "ORDER BY spending_rank ASC";

        return jdbcTemplate.query(sql, new UserRankingRowMapper());
    }

    /**
     * Intermediate Query 2: Window Functions (ROW_NUMBER & Running Cumulative Sum)
     * Calculates customer transaction sequence and running total over time.
     */
    @Override
    public List<OrderCumulativeReportRow> findCumulativeSpentReport() {
        String sql = "SELECT "
            + "    o.id AS order_id, "
            + "    o.user_id, "
            + "    o.status, "
            + "    o.total_amount, "
            + "    o.created_at, "
            + "    ROW_NUMBER() OVER (PARTITION BY o.user_id ORDER BY o.created_at ASC) AS order_sequence, "
            + "    SUM(o.total_amount) OVER (PARTITION BY o.user_id ORDER BY o.created_at ASC ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS cumulative_spent "
            + "FROM orders o "
            + "ORDER BY o.user_id, o.created_at ASC";

        return jdbcTemplate.query(sql, new OrderCumulativeRowMapper());
    }

    /**
     * Intermediate Query 3: Multi-table JOIN with Aggregation and HAVING
     * Analyzes product performance across orders filtered by minimum volume sold.
     */
    @Override
    public List<ProductPerformanceReportRow> findProductPerformance(int minQuantity) {
        String sql = "SELECT "
            + "    oi.product_name, "
            + "    COUNT(DISTINCT o.id) AS orders_count, "
            + "    SUM(oi.quantity) AS total_quantity_sold, "
            + "    SUM(oi.price * oi.quantity) AS total_revenue, "
            + "    AVG(oi.price) AS average_price "
            + "FROM order_items oi "
            + "INNER JOIN orders o ON o.id = oi.order_id "
            + "WHERE o.status IN ('NEW', 'PAID', 'COMPLETED') "
            + "GROUP BY oi.product_name "
            + "HAVING SUM(oi.quantity) >= ? "
            + "ORDER BY total_revenue DESC";

        return jdbcTemplate.query(sql, new ProductPerformanceRowMapper(), minQuantity);
    }
}
