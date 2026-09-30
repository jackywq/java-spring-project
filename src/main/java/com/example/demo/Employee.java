package com.example.demo;

public record Employee(
        int id,
        String name,
        String department,
        String position,
        int age,
        String email,
        String hireDate) {
}
