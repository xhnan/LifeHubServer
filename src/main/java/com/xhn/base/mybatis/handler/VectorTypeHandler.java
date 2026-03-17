package com.xhn.base.mybatis.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles PostgreSQL pgvector text values such as "[0.1,0.2]".
 */
public class VectorTypeHandler extends BaseTypeHandler<List<Double>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Double> parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, toVectorText(parameter));
    }

    @Override
    public List<Double> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseVectorText(rs.getString(columnName));
    }

    @Override
    public List<Double> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseVectorText(rs.getString(columnIndex));
    }

    @Override
    public List<Double> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseVectorText(cs.getString(columnIndex));
    }

    private List<Double> parseVectorText(String text) {
        if (text == null) {
            return null;
        }
        String value = text.trim();
        if (value.isEmpty()) {
            return Collections.emptyList();
        }
        if (value.charAt(0) == '[' && value.charAt(value.length() - 1) == ']') {
            value = value.substring(1, value.length() - 1);
        }
        if (value.isBlank()) {
            return Collections.emptyList();
        }
        String[] parts = value.split(",");
        List<Double> result = new ArrayList<>(parts.length);
        for (String part : parts) {
            String item = part.trim();
            if (!item.isEmpty()) {
                result.add(Double.valueOf(item));
            }
        }
        return result;
    }

    private String toVectorText(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return "[]";
        }
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(values.get(i));
        }
        return builder.append(']').toString();
    }
}
