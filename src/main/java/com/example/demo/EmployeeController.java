package com.example.demo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name", "age", "hireDate");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final List<Employee> employees;

    public EmployeeController() {
        try (InputStream is = new ClassPathResource("data/employees.json").getInputStream()) {
            this.employees = MAPPER.readValue(is, new TypeReference<List<Employee>>() {});
        } catch (IOException e) {
            throw new RuntimeException("无法加载员工数据", e);
        }
    }

    /** GET /api/employees — 人员列表查询（核心接口） */
    @GetMapping("/employees")
    public ApiResponse<Map<String, Object>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) Integer limit) {

        // 1. 过滤
        List<Employee> filtered = employees.stream()
                .filter(e -> matchesName(e, name))
                .filter(e -> matchesDepartment(e, department))
                .filter(e -> matchesMinAge(e, minAge))
                .filter(e -> matchesMaxAge(e, maxAge))
                .toList();

        // 2. 统计（在 limit 截断之前）
        int total = filtered.size();
        Map<String, Object> stats = ApiResponse.computeStats(filtered);

        // 3. 排序（白名单校验，非法字段回退默认 id）
        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "id";
        Comparator<Employee> comparator = resolveComparator(safeSortBy, sortOrder);
        List<Employee> sorted = filtered.stream().sorted(comparator).toList();

        // 4. 条数限制（0 / null / "all" = 不限制）
        List<Employee> result = applyLimit(sorted, limit);

        return ApiResponse.ok(ApiResponse.employeeData(result, total, stats));
    }

    /** GET /api/departments — 部门下拉选项 */
    @GetMapping("/departments")
    public ApiResponse<Map<String, Object>> departments() {
        List<String> departments = employees.stream()
                .map(Employee::department)
                .distinct()
                .sorted()
                .toList();
        return ApiResponse.ok(Map.of("departments", departments));
    }

    // ─── 过滤方法 ─────────────────────────────────────────────

    private boolean matchesName(Employee e, String name) {
        if (name == null || name.isBlank()) return true;
        return e.name().toLowerCase(Locale.ROOT).contains(name.trim().toLowerCase(Locale.ROOT));
    }

    private boolean matchesDepartment(Employee e, String department) {
        if (department == null || department.isBlank() || "全部部门".equals(department.trim())) return true;
        return e.department().equals(department.trim());
    }

    private boolean matchesMinAge(Employee e, Integer minAge) {
        return minAge == null || e.age() >= minAge;
    }

    private boolean matchesMaxAge(Employee e, Integer maxAge) {
        return maxAge == null || e.age() <= maxAge;
    }

    // ─── 排序 ─────────────────────────────────────────────────

    private Comparator<Employee> resolveComparator(String sortBy, String sortOrder) {
        Comparator<Employee> comparator = switch (sortBy) {
            case "name" -> Comparator.comparing(Employee::name, String.CASE_INSENSITIVE_ORDER);
            case "age" -> Comparator.comparing(Employee::age);
            case "hireDate" -> Comparator.comparing(Employee::hireDate);
            default -> Comparator.comparing(Employee::id);
        };
        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }
        return comparator;
    }

    // ─── 分页 ─────────────────────────────────────────────────

    private List<Employee> applyLimit(List<Employee> list, Integer limit) {
        if (limit == null || limit <= 0) return list;
        return list.subList(0, Math.min(limit, list.size()));
    }
}
