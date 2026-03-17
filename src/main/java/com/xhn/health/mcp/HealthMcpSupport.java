package com.xhn.health.mcp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class HealthMcpSupport {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };
    private static final TypeReference<List<Map<String, Object>>> LIST_MAP_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;

    public HealthMcpSupport(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public LocalDate[] dateRange(int days) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(Math.max(days, 1) - 1L);
        return new LocalDate[]{start, end};
    }

    public LocalDate parseDate(String value) {
        return LocalDate.parse(value);
    }

    public Map<String, Object> toMap(Object value) {
        if (value == null) {
            return Collections.emptyMap();
        }
        return objectMapper.convertValue(value, MAP_TYPE);
    }

    public List<Map<String, Object>> toList(Object value) {
        if (value == null) {
            return Collections.emptyList();
        }
        return objectMapper.convertValue(value, LIST_MAP_TYPE);
    }

    public BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public double round(BigDecimal value) {
        return safe(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
