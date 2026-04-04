# Java Spring 人员列表示例

## 项目简介

这是一个基于 Spring Boot 的简单示例项目，提供人员列表查询接口。

当前已实现功能：

- 获取人员列表
- 返回 JSON 格式数据
- 支持通过配置端口访问接口

## 运行环境

- JDK 17
- Gradle

## 启动项目

在项目根目录执行：

```bash
./gradlew bootRun
```

也可以在 IntelliJ IDEA 中直接右键运行启动类：

1. 打开 [DemoApplication.java](file:///Users/wangquan/Downloads/java-spring-demo/src/main/java/com/example/demo/DemoApplication.java)
2. 在类文件编辑区右键
3. 选择 `Run 'DemoApplication.main()'`
4. 等待控制台出现 Spring Boot 启动成功日志
5. 然后访问 `http://localhost:8099/persons`

启动成功后，服务默认监听：

```text
http://localhost:8099
```

## 接口说明

### 获取人员列表

- 请求方式：`GET`
- 请求路径：`/persons`

完整访问地址：

```text
http://localhost:8099/persons
```

也可以使用命令行调用：

```bash
curl http://localhost:8099/persons
```

## 返回示例

```json
[
  {
    "id": 1,
    "name": "张三",
    "age": 28,
    "department": "研发部"
  },
  {
    "id": 2,
    "name": "李四",
    "age": 32,
    "department": "产品部"
  },
  {
    "id": 3,
    "name": "王五",
    "age": 26,
    "department": "运营部"
  }
]
```

## 主要代码位置

- 启动类：[DemoApplication.java](file:///Users/wangquan/Downloads/java-spring-demo/src/main/java/com/example/demo/DemoApplication.java)
- 控制器：[PersonController.java](file:///Users/wangquan/Downloads/java-spring-demo/src/main/java/com/example/demo/PersonController.java)
- 数据模型：[Person.java](file:///Users/wangquan/Downloads/java-spring-demo/src/main/java/com/example/demo/Person.java)
- 配置文件：[application.properties](file:///Users/wangquan/Downloads/java-spring-demo/src/main/resources/application.properties)

## 修改端口

当前端口配置位于 [application.properties](file:///Users/wangquan/Downloads/java-spring-demo/src/main/resources/application.properties)：

```properties
server.port=8099
```

如果你想改成其他端口，比如 `8081`，修改为：

```properties
server.port=8081
```

修改后重新启动项目，再通过新端口访问接口。

## 运行测试

执行以下命令：

```bash
./gradlew test
```
