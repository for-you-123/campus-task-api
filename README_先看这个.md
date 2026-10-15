# CampusTask 完整源码（Day32 汇总整理版）

> **这不是对你电脑当前 IDEA 文件的实时备份。** 本项目从你曾经上传的 `campus-task-api.zip`（Day28）和之后的 Day29、Day30、Day32 补丁合并而成。你没有上传的本地修改（例如截图中出现但原始 ZIP 中不存在的 `Authentication.java`）无法还原，请先自行备份原项目。

## 一、版本与范围

- Java：17
- Spring Boot：**3.3.5**（依照你近期测试运行日志选择；原始上传的 pom.xml 是 4.1.1。请注意这次统一版本是明确的版本调整，不是误写）
- Maven + Maven Wrapper，Spring MVC、Spring Security、Spring Data JPA、MySQL 8、JJWT 0.12.6、JUnit 5、Mockito、MockMvc
- 完整保留原有 TaskService 与 TaskRepository 的创建、筛选、分页、修改、删除、任务所有权逻辑。
- Day29：`TaskController` 和 `AuthController` 改成 `@AuthenticationPrincipal` 统一读取认证身份；JWT 在 `JwtAuthenticationFilter` 中验证。
- Day30：用户默认 `USER`，`ADMIN` 才可查询所有用户/指定用户；任务仍限制所有权。
- Day31：增加 TaskSimpleTest 和 TaskServiceTest，位于 `src/test/java`。
- Day32：增加 MockMvc `TaskControllerTest`，测试 401、GET 200、POST 200、参数 400；引入真实 `SecurityConfig`，Service/AuthService 采用 Mock。
- 日志改用标准 SLF4J Logger，而不需要 IDEA 启用 Lombok Annotation Processing。
- 数据库配置只读取环境变量，不包含你以前上传的明文数据库密码或 JWT 密钥。

## 二、IDEA 导入（不要覆盖原项目）

1. 先把电脑上现在的 `campus-task-api` 项目复制备份到其他目录。
2. 解压本 ZIP，得到顶层 `campus-task-api` 目录，**不要把文件复制到原项目里混用**。
3. IntelliJ IDEA：`File` → `Open`，选择解压出来的 `campus-task-api/pom.xml` 或该项目目录，以 Maven 项目方式导入。
4. 等待 IDEA 完成 Maven Reload，项目 SDK 选 Java 17（不要使用源代码版本 17 以外的编译设置）。
5. 检查 `src/main/java/com/an/campustaskapi/task/TaskController.java` 是正式 Controller；`src/test/java/com/an/campustaskapi/task/TaskControllerTest.java` 是测试。绝不能把 Test 类粘贴到 main 中。

## 三、运行前必须设置环境变量

在 IDEA：`Run` → `Edit Configurations` → 你的 Spring Boot 运行配置 → `Environment variables`，设置：

- `DB_PASSWORD`：你本地 MySQL 的真实密码。**不要发给别人，不要提交 Git**。
- `DB_USER`：可选；默认 `root`，建议使用本地专用数据库账号。
- `DB_URL`：可选；默认 `jdbc:mysql://localhost:3306/campus_task`。
- `JWT_SECRET_BASE64`：必填；应为**随机至少 32 字节**密钥材料的 Base64 编码。生产环境须安全管理密钥；不要分享给别人。

提示：IDEA 运行配置中的环境变量不会自动出现在 Windows PowerShell 里。如果你在 PowerShell 运行 `mvnw.cmd test` 或启动应用，需要在该终端另行设置变量。

### 第一次连接 MySQL

创建数据库（如尚未存在）：

```sql
CREATE DATABASE IF NOT EXISTS campus_task CHARACTER SET utf8mb4;
```

旧项目已有 `users` 表时，请**先备份数据库**并查看 `db/day30_user_role_migration.sql`。只有确认缺少 `role` 列时，才添加它，禁止重复 `ALTER TABLE`。`spring.jpa.hibernate.ddl-auto=update` 仅供本地学习，并不能取代正式的数据库迁移方案。

## 四、运行测试

在 IDEA 左侧 `src/test/java/com/an/campustaskapi/task`：

1. 先右键 `TaskSimpleTest` → Run。
2. 再右键 `TaskServiceTest` → Run。
3. 最后右键 `TaskControllerTest` → Run。

也可以在项目根目录 PowerShell 运行：

```powershell
.\mvnw.cmd test
```

`TaskControllerTest` 的模拟 JWT 不是有效 JWT：过滤器在测试中会从 `AuthService` Mock 获得模拟用户，故不需要提供真实密钥或真实数据库。它**不检验 JWT 签名算法本身**。

## 五、主要 API

- `POST /users`：注册普通用户（不接受客户端选择 ADMIN）
- `POST /auth/login`：登录并获得 JWT
- `GET /auth/me`：查询当前登录用户
- `GET /tasks`：查询属于自己的任务，支持 `?status=TODO`
- `GET /tasks/page`：按用户和状态分页
- `POST /tasks`：创建任务
- `GET /tasks/{id}`：查询自己的任务
- `PUT /tasks/{id}`：修改自己的任务
- `PATCH /tasks/{id}/status`：修改任务状态
- `DELETE /tasks/{id}`：删除自己的任务
- `GET /users`、`GET /users/{id}`：必须拥有 ADMIN 角色

除注册和登录外，接口均要求携带有效 JWT；用户角色为 USER/ADMIN，并不代表拥有其他用户的 Task 所有权。

## 六、已知限制与安全提醒

- **无法读取你电脑上尚未上传的最新编辑内容。** 尤其截图中的 `Authentication.java` 不在原始源码包中，本包没有凭空创建它。
- 因执行环境无法连接 Maven Central，**尚未真实完成 Maven 编译或 JUnit 测试**。已经做过：Java 源文件语法解析、pom.xml XML 解析、目录与重复类检查、ZIP 完整性检查。务必以你本地 Maven 运行结果为准。
- 本项目沿用教学阶段的 Entity 直接返回等结构，仍需进一步完善响应 DTO、数据库迁移、权限测试和部署安全。
- 不包含 `.git`、`.idea`、`target` 等机器相关缓存/历史，避免携带过期编译产物与潜在秘密；源码、pom.xml、Maven Wrapper、配置和测试齐全。
- **千万不要直接覆盖你正在使用的项目目录**。先在全新目录解压导入并验证，再决定是否迁移你本地的其他修改。
