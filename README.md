# Blog API

基于 Spring Boot 3 + MyBatis + MySQL 的博客后端项目。

<img width="3056" height="1588" alt="image" src="https://github.com/user-attachments/assets/6863c153-9940-49f1-928e-b67744988be6" />


## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.3.12 | 基础框架 |
| MyBatis | 3.0.4 | ORM 框架 |
| MySQL | 8+ | 数据库 |
| JWT (jjwt) | 0.12.6 | 鉴权 |
| Spring Security Crypto | - | BCrypt 密码加密 |
| SpringDoc OpenAPI | 2.6.0 | 接口文档 |
| Lombok | - | 简化实体类 |
| Thymeleaf | - | 模板引擎 |

## 功能模块

### 后台管理

- **认证管理** — 管理员登录/退出、JWT 鉴权、个人资料与密码修改
- **文章管理** — 文章增删改查、发布/下线/草稿状态切换、置顶
- **分类管理** — 分类的增删改查、排序
- **标签管理** — 标签的增删改查
- **站点配置** — 站点名称、Logo、描述、公告、页脚信息、社交链接
- **文件上传** — 图片上传与本地存储
- **仪表盘** — 统计数据概览
- **操作日志** — AOP 自动记录后台操作日志

### 前台展示

- **文章列表** — 分页、关键词搜索、按分类/标签筛选
- **文章详情** — 文章内容展示、浏览量统计
- **文章归档** — 按时间归档
- **分类/标签列表** — 前台分类与标签查询
- **站点信息** — 前台获取站点配置

## 项目结构

```
src/main/java/com/example/blog/
├── annotation/          # 自定义注解（OperationLog）
├── aspect/              # AOP 切面（操作日志）
├── common/
│   ├── constant/        # 常量定义
│   ├── exception/       # 全局异常处理 & BusinessException
│   └── result/          # 统一响应 Result / ResultCode / PageResult
├── config/              # 配置类（Web / JWT / 文件存储 / OpenAPI / 密码）
├── controller/
│   ├── admin/           # 后台接口（需鉴权）
│   └── portal/          # 前台接口（无需鉴权）
├── dto/                 # 请求参数
├── entity/              # 数据库实体
├── mapper/              # MyBatis Mapper 接口
├── service/             # 业务逻辑接口
│   └── impl/            # 业务逻辑实现
├── util/                # 工具类（JwtUtil / LoginUserContext）
└── vo/                  # 响应视图对象
```

依次执行以下脚本：

```bash
mysql -u root -p < sql/blog_schema.sql
mysql -u root -p < sql/blog_schema_v2_site_config.sql
mysql -u root -p < sql/blog_schema_v3_operation_log.sql
```

### 2. 修改配置文件

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your-password
```

### 3. 启动项目

```bash
mvn spring-boot:run
```

或打包后运行：

```bash
mvn clean package
java -jar target/blog-0.0.1-SNAPSHOT.jar
```

## 默认账号

| 字段 | 值 |
|------|------|
| 用户名 | `admin` |
| 密码 | `admin123` |

首次登录后，系统会自动将明文密码升级为 BCrypt 加密格式。

## 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

## 常用地址

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- 健康检查: http://localhost:8080/health

## 文件上传

- 默认上传目录：`项目根目录/uploads`
- 访问前缀：`/uploads/`
- 文件大小限制：5MB

## 鉴权说明

后台接口（除 `/admin/auth/login`）需携带请求头：

```
Authorization: Bearer <token>
```

## 推荐联调顺序

1. 登录获取 token
2. 查询/修改站点配置
3. 新增分类与标签
4. 新增文章并上传封面图
5. 调前台文章列表和详情接口
