# 三个文件的关联关系详解

这三个文件是一个标准 Spring Boot Web 项目的核心组成部分，它们分工明确、层层递进，共同完成一个完整的后端接口服务。下面用“角色分工 + 调用链路 + 代码示例”的方式，把它们之间的关系讲清楚。

## 一、三个文件各自的角色定位

| 文件名 | 核心角色 | 核心作用 |
| --- | --- | --- |
| DemoApplication.java | 项目启动入口（总指挥） | Spring Boot 项目的启动类，负责启动整个应用、扫描并加载所有组件 |
| Person.java | 数据模型（数据载体） | 定义业务实体，例如“人”这个对象，用来封装和传递数据 |
| PersonController.java | 接口控制器（对外窗口） | 处理前端或外部的 HTTP 请求，调用业务逻辑并返回响应 |

## 二、核心关联链路

### 1. 第一步：DemoApplication.java 启动，扫描并加载另外两个文件

`DemoApplication.java` 上的 `@SpringBootApplication` 注解，自带 `@ComponentScan` 组件扫描能力。

它会自动扫描当前包 `com.example.demo` 及其所有子包下的 Spring 组件：

- `PersonController.java` 上标注了 `@RestController`，会被扫描到并注册到 Spring 容器中
- `Person.java` 是普通实体类，不需要 Spring 管理，但会被 `PersonController` 引用，作为数据载体使用

启动代码核心逻辑如下：

```java
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

### 2. 第二步：PersonController.java 依赖 Person.java 作为数据模型

`PersonController` 是接口层，需要用 `Person` 实体来封装请求或响应数据，实现“对象化”的接口开发，而不是使用零散的参数。

典型代码示例如下。

#### Person.java（实体类）

```java
package com.example.demo;

public class Person {
    private Long id;
    private String name;
    private Integer age;

    public Person() {}

    public Person(Long id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
```

#### PersonController.java（控制器，依赖 Person）

```java
package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController {

    @GetMapping("/person")
    public Person getPerson() {
        return new Person(1L, "张三", 25);
    }
}
```

### 3. 第三步：三个文件协同，完成接口的完整生命周期

整个调用过程可以分成四个阶段：

1. 启动阶段：运行 `DemoApplication.main()`，Spring 容器启动，扫描到 `PersonController` 并完成注册，同时加载 `Person` 类的定义
2. 请求阶段：前端访问 `http://localhost:8080/person`，Spring MVC 把请求路由到 `PersonController.getPerson()` 方法
3. 处理阶段：`PersonController` 创建 `Person` 对象并填充数据
4. 响应阶段：Spring 自动将 `Person` 对象序列化为 JSON，并返回给前端

返回结果示例如下：

```json
{
  "id": 1,
  "name": "张三",
  "age": 25
}
```
