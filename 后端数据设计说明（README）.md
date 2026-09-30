# 人事管理系统 — 后端数据设计

> 对应前端页面：人员列表能力扩展示例（姓名筛选 / 部门过滤 / 年龄区间 / 排序 / 条数限制 / 结果集统计）。
> 本项目以 Vercel Serverless Function 形式提供，零配置可部署。

## 一、目录结构

```
hr-backend-design/
├── api/
│   ├── employees.js       # GET /api/employees   人员列表查询（核心接口）
│   └── departments.js      # GET /api/departments 部门下拉选项
├── data/
│   └── employees.json      # 员工种子数据（40 条，8 个部门）
├── test.js                 # 本地逻辑验证脚本（node test.js）
└── README.md               # 本文件
```

## 二、数据模型

### 2.1 员工表 `employees`

| 字段       | 类型    | 说明                          | 示例                |
| ---------- | ------- | ----------------------------- | ------------------- |
| `id`       | int     | 主键，自增编号                | `1`                 |
| `name`     | string  | 姓名（支持模糊搜索）          | `"张伟"`            |
| `department` | string | 所属部门（外键 → 部门表）     | `"技术研发部"`      |
| `position` | string  | 职位                          | `"前端工程师"`      |
| `age`      | int     | 年龄（用于区间筛选与统计）    | `29`                |
| `email`    | string  | 邮箱                          | `zhangwei@example.com` |
| `hireDate` | string  | 入职日期 `YYYY-MM-DD`         | `"2021-03-15"`      |

### 2.2 部门表 `departments`（当前由员工数据去重派生）

| 字段          | 类型   | 说明            |
| ------------- | ------ | --------------- |
| `name`        | string | 部门名称（唯一）|

当前共 8 个部门：财务部、产品部、技术研发部、人力资源部、设计部、市场部、销售部、运营部。

> 当前版本用 JSON 文件作为数据源（适合演示 / 小数据量）。
> 生产环境建议迁移到 PostgreSQL / MySQL / Supabase，表结构与上面字段一一对应即可，SQL 示例见第五节。

## 三、API 契约

### 3.1 获取人员列表

```
GET /api/employees
```

#### Query 参数

| 参数          | 类型   | 必填 | 说明                                                        |
| ------------- | ------ | ---- | ----------------------------------------------------------- |
| `name`        | string | 否   | 姓名关键字，模糊匹配（大小写不敏感）                        |
| `department`  | string | 否   | 部门精确筛选；传空或 `"全部部门"` 表示不过滤                 |
| `minAge`      | int    | 否   | 最小年龄，闭区间 `age >= minAge`                            |
| `maxAge`      | int    | 否   | 最大年龄，闭区间 `age <= maxAge`                            |
| `sortBy`      | string | 否   | 排序字段：`id` / `name` / `age` / `hireDate`，默认 `id`    |
| `sortOrder`   | string | 否   | 排序方向：`asc` / `desc`，默认 `asc`                        |
| `limit`       | int    | 否   | 返回条数；传 `0` / `all` / 不传 = 不限制                    |

#### 响应体

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "张伟",
        "department": "技术研发部",
        "position": "前端工程师",
        "age": 29,
        "email": "zhangwei@example.com",
        "hireDate": "2021-03-15"
      }
    ],
    "total": 40,
    "stats": {
      "count": 40,
      "avgAge": 31.3,
      "minAge": 24,
      "maxAge": 45,
      "departments": ["财务部", "产品部", "技术研发部", "人力资源部", "设计部", "市场部", "销售部", "运营部"]
    }
  }
}
```

#### 字段对应前端面板

| 前端控件         | Query 参数                | 响应字段                  |
| ---------------- | ------------------------- | ------------------------- |
| 姓名关键字       | `name`                    | —                         |
| 部门下拉         | `department`              | `stats.departments`       |
| 最小年龄         | `minAge`                  | `stats.minAge`            |
| 最大年龄         | `maxAge`                  | `stats.maxAge`            |
| 排序字段         | `sortBy`                  | —                         |
| 排序方向         | `sortOrder`               | —                         |
| 结果条数         | `limit`                   | `data.list` 长度          |
| 「共 N 条数据」  | —                         | `data.total`              |
| 当前结果数       | —                         | `stats.count`             |
| 平均年龄         | —                         | `stats.avgAge`            |
| 年龄范围         | —                         | `stats.minAge` ~ `stats.maxAge` |
| 涉及部门         | —                         | `stats.departments`       |

> **统计口径说明**：`stats` 基于**完整过滤结果集**计算（在 `limit` 截断之前），
> 这样前端即使只展示前 N 条卡片，顶部统计仍反映整个筛选条件下的全貌。
> 若希望统计只反映当前页，把 `stats` 计算移到 `slice` 之后即可。

### 3.2 获取部门下拉选项

```
GET /api/departments
```

响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "departments": ["财务部", "产品部", "技术研发部", "人力资源部", "设计部", "市场部", "销售部", "运营部"]
  }
}
```

## 四、部署到 Vercel

1. 把整个目录推到 GitHub 仓库。
2. 在 Vercel 中 **New Project** → 导入该仓库。
3. 无需任何环境变量，直接 **Deploy**。
4. 部署后访问：
   - `https://<your-project>.vercel.app/api/employees`
   - `https://<your-project>.vercel.app/api/departments`

本地调试：

```bash
npm i -g vercel
vercel dev
# 访问 http://localhost:3000/api/employees?department=技术研发部&minAge=28
```

## 五、迁移到生产数据库（可选）

当前 JSON 文件适合演示。数据量上来后，把 `api/employees.js` 里的 `require('../data/employees.json')`
换成数据库查询即可，业务逻辑（过滤 / 排序 / 统计）保持不变。以 PostgreSQL 为例：

```sql
CREATE TABLE departments (
  id   SERIAL PRIMARY KEY,
  name TEXT UNIQUE NOT NULL
);

CREATE TABLE employees (
  id          SERIAL PRIMARY KEY,
  name        TEXT NOT NULL,
  department  TEXT NOT NULL REFERENCES departments(name),
  position    TEXT,
  age         INT  CHECK (age >= 16 AND age <= 80),
  email       TEXT UNIQUE,
  hire_date   DATE
);

-- 对应 /api/employees 的 SQL 骨架
SELECT * FROM employees
WHERE ($1 = '' OR name ILIKE '%' || $1 || '%')
  AND ($2 = '' OR department = $2)
  AND ($3 IS NULL OR age >= $3)
  AND ($4 IS NULL OR age <= $4)
ORDER BY ${sortBy} ${sortOrder}
LIMIT $5;
```

## 六、验证结果

本地运行 `node test.js` 已覆盖 9 组用例：

| 用例                       | 结果                                  |
| -------------------------- | ------------------------------------- |
| 无参                       | 40 条，平均年龄 31.3，8 个部门       |
| 姓名模糊「张」             | 命中 1 条（张伟）                     |
| 部门=技术研发部            | 命中 12 条                            |
| 年龄 26~32                 | 命中 22 条                            |
| 部门+年龄组合               | 命中 10 条                            |
| 按年龄降序                 | 45 岁梁燕排第一                       |
| limit=5                    | 返回 5 条，total 仍为 40              |
| 不匹配组合                 | 0 条，stats 全 0 / 空数组             |
| 非法 sortBy 白名单拦截      | 回退默认 id 排序，不报错              |
