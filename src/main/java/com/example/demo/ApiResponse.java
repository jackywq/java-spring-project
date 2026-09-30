package com.example.demo;

import java.util.List;
import java.util.Map;

public record ApiResponse<T>(int code, String message, T data) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(0, "ok", data);
    }

    /** 构造 data 层：list + total + stats */
    public static Map<String, Object> employeeData(List<Employee> list, int total, Map<String, Object> stats) {
        return Map.of("list", list, "total", total, "stats", stats);
    }

    /** 基于过滤后的完整结果集计算 stats（在 limit 截断之前） */
    public static Map<String, Object> computeStats(List<Employee> filtered) {
        if (filtered.isEmpty()) {
            return Map.of(
                    "count", 0,
                    "avgAge", 0,
                    "minAge", 0,
                    "maxAge", 0,
                    "departments", List.of());
        }
        int count = filtered.size();
        int totalAge = filtered.stream().mapToInt(Employee::age).sum();
        double avgAge = Math.round(totalAge * 10.0 / count) / 10.0;
        int minAge = filtered.stream().mapToInt(Employee::age).min().orElse(0);
        int maxAge = filtered.stream().mapToInt(Employee::age).max().orElse(0);
        List<String> departments = filtered.stream()
                .map(Employee::department)
                .distinct()
                .sorted()
                .toList();
        return Map.of(
                "count", count,
                "avgAge", avgAge,
                "minAge", minAge,
                "maxAge", maxAge,
                "departments", departments);
    }
}
