package com.example.demo;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/persons")
public class PersonController {
    private final List<Person> people = List.of(
            new Person(1L, "张三", 28, "研发部"),
            new Person(2L, "李四", 32, "产品部"),
            new Person(3L, "王五", 26, "运营部"));

    @GetMapping
    public List<Person> list() {
        return people;
    }
}
