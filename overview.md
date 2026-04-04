# 能力概览

## 概述

当前项目已经从最初的基础人员列表示例，扩展为一套同时支持本地 Spring Boot 运行和 Vercel 部署展示的能力示例。项目包含后端接口、Vercel Serverless 接口，以及一个可直接交互的静态展示页。

## 当前能力

### 1. 基础人员列表

- 支持获取全部人员列表
- 返回标准 JSON 数据
- 包含 `id`、`name`、`age`、`department` 字段

### 2. 列表筛选与排序

`/persons` 接口已支持以下查询能力：

- `name`：按姓名关键字筛选
- `department`：按部门过滤
- `minAge`：按最小年龄过滤
- `maxAge`：按最大年龄过滤
- `sortBy`：按 `id`、`name`、`age` 排序
- `order`：支持 `asc` 和 `desc`
- `limit`：限制返回条数

示例：

```text
GET /persons?department=研发部&sortBy=age&order=desc&limit=1
```

### 3. 单人详情查询

支持根据人员编号查询单个对象：

```text
GET /persons/{id}
```

示例：

```text
GET /persons/2
```

### 4. 部门列表查询

支持返回当前数据中的全部部门列表：

```text
GET /persons/departments
```

### 5. 统计能力

支持返回当前人员数据的聚合统计信息：

- 总人数
- 平均年龄
- 最小年龄
- 最大年龄
- 各部门人数分布

接口如下：

```text
GET /persons/stats
```

### 6. Vercel 部署支持

项目已经补齐适用于 Vercel 的部署结构：

- `public/index.html`：静态展示页
- `api/persons.js`：Vercel Serverless 接口
- `vercel.json`：部署和路由配置

部署后可以直接访问：

- 首页：`/`
- 接口：`/persons`

## 前端交互能力

首页已经支持以下交互：

- 输入姓名关键字进行实时筛选
- 选择部门过滤结果
- 设置年龄区间
- 选择排序字段与排序方向
- 限制结果条数
- 展示当前结果集统计信息
- 显示接口原始 JSON 响应

## 主要文件

- Spring Boot 控制器：[PersonController.java](file:///Users/wangquan/Downloads/java-spring-project/src/main/java/com/example/demo/PersonController.java)
- 数据模型：[Person.java](file:///Users/wangquan/Downloads/java-spring-project/src/main/java/com/example/demo/Person.java)
- Vercel 接口：[persons.js](file:///Users/wangquan/Downloads/java-spring-project/api/persons.js)
- 展示页面：[index.html](file:///Users/wangquan/Downloads/java-spring-project/public/index.html)
- 部署配置：[vercel.json](file:///Users/wangquan/Downloads/java-spring-project/vercel.json)

## 适用场景

这套能力目前适合用于：

- Spring Boot Web 基础教学
- REST 接口设计演示
- 列表筛选与排序示例
- Vercel 部署演示
- 前后端联调展示

## 后续可继续扩展

如果继续增强，下一步可以扩展为：

- 新增人员
- 编辑人员
- 删除人员
- 分页查询
- 数据持久化到数据库
- 登录鉴权与角色控制
