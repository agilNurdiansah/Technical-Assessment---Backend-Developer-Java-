package com.company.orderservice.infrastructure.persistence.nativeq.mapper;

import com.company.orderservice.application.dto.report.ProductPerformanceReportRow;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ProductPerformanceRowMapper implements RowMapper<ProductPerformanceReportRow> {
    @Override
    public ProductPerformanceReportRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        ProductPerformanceReportRow row = new ProductPerformanceReportRow();
        row.setProductName(rs.getString("product_name"));
        row.setOrdersCount(rs.getLong("orders_count"));
        row.setTotalQuantitySold(rs.getLong("total_quantity_sold"));
        row.setTotalRevenue(rs.getBigDecimal("total_revenue"));
        row.setAveragePrice(rs.getBigDecimal("average_price"));
        return row;
    }
}
