package com.example.demo;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/persons")
public class PersonController {
    private final List<Person> people = List.of(
            new Person(1L, "张三", 28, "研发部"),
            new Person(2L, "李四", 32, "产品部"),
            new Person(3L, "王五", 26, "运营部"));

    @GetMapping
    public List<Person> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) Integer limit) {
        return people.stream()
                .filter(person -> matchesName(person, name))
                .filter(person -> matchesDepartment(person, department))
                .filter(person -> matchesMinAge(person, minAge))
                .filter(person -> matchesMaxAge(person, maxAge))
                .sorted(resolveComparator(sortBy, order))
                .limit(resolveLimit(limit))
                .toList();
    }

    @GetMapping("/{id}")
    public Person getById(@PathVariable Long id) {
        return people.stream()
                .filter(person -> person.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "未找到对应人员"));
    }

    @GetMapping("/departments")
    public List<String> departments() {
        return people.stream()
                .map(Person::department)
                .distinct()
                .sorted()
                .toList();
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        IntSummary stats = summarizeAges();
        Map<String, Long> departmentCounts = people.stream()
                .collect(Collectors.groupingBy(Person::department, LinkedHashMap::new, Collectors.counting()));

        return Map.of(
                "total", people.size(),
                "averageAge", stats.averageAge(),
                "youngestAge", stats.minAge(),
                "oldestAge", stats.maxAge(),
                "departmentCounts", departmentCounts);
    }

    private boolean matchesName(Person person, String name) {
        if (name == null || name.isBlank()) {
            return true;
        }
        return person.name().toLowerCase(Locale.ROOT).contains(name.trim().toLowerCase(Locale.ROOT));
    }

    private boolean matchesDepartment(Person person, String department) {
        if (department == null || department.isBlank()) {
            return true;
        }
        return person.department().equalsIgnoreCase(department.trim());
    }

    private boolean matchesMinAge(Person person, Integer minAge) {
        return minAge == null || person.age() >= minAge;
    }

    private boolean matchesMaxAge(Person person, Integer maxAge) {
        return maxAge == null || person.age() <= maxAge;
    }

    private Comparator<Person> resolveComparator(String sortBy, String order) {
        Comparator<Person> comparator = switch (sortBy.toLowerCase(Locale.ROOT)) {
            case "name" -> Comparator.comparing(Person::name, String.CASE_INSENSITIVE_ORDER);
            case "age" -> Comparator.comparing(Person::age);
            default -> Comparator.comparing(Person::id);
        };

        if ("desc".equalsIgnoreCase(order)) {
            return comparator.reversed();
        }
        return comparator;
    }

    private long resolveLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return Long.MAX_VALUE;
        }
        return limit;
    }

    private IntSummary summarizeAges() {
        int totalAge = people.stream()
                .mapToInt(Person::age)
                .sum();
        int minAge = people.stream()
                .mapToInt(Person::age)
                .min()
                .orElse(0);
        int maxAge = people.stream()
                .mapToInt(Person::age)
                .max()
                .orElse(0);
        double averageAge = people.isEmpty() ? 0 : Math.round((totalAge * 10.0 / people.size())) / 10.0;
        return new IntSummary(minAge, maxAge, averageAge);
    }

    private record IntSummary(int minAge, int maxAge, double averageAge) {
    }
}
