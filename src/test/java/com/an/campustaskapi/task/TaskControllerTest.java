package com.an.campustaskapi.task;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Day32：TaskController 的 HTTP 层自动化测试。
 *
 * @WebMvcTest：只加载 MVC 测试需要的组件，不启动真实 MySQL。
 * @Import(SecurityConfig.class)：引入我们项目真实的安全配置，
 * 避免测试时误用默认的 CSRF 规则而导致 POST 返回 403。
 *
 * TaskService、AuthService 都是 Mockito 模拟对象。
 * 这样可以测试 Controller 与安全过滤器的配合，而不需要签发真实 JWT。
 *
 * 测试 Day29+ 的 Controller：使用 @AuthenticationPrincipal 获得当前用户。
 * Token 验证由 JwtAuthenticationFilter 执行，AuthService 用 Mock 隔离。
 */
@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
class TaskControllerTest {

    private static final Long USER_ID = 5L;

    // 只是用于模拟的字符串，不是真实 JWT，也不是密钥。
    private static final String TEST_TOKEN = "fake-day32-token";
    private static final String AUTHORIZATION = "Bearer " + TEST_TOKEN;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private AuthService authService;

    /**
     * 给 AuthService Mock 预设返回值。
     *
     * 进入 Controller 前，JwtAuthenticationFilter 可能调用：
     *     getUserFromToken("fake-day32-token")
     *
     * Day29+ Controller 不会再次读取 Authorization Header。
     * 此处只模拟过滤器的验证结果，不测试真正的 JWT 签名算法。
     */
    private void prepareAuthenticatedUser() {
        User user = new User(USER_ID, "day32_user", "unused-password-hash");

        when(authService.getUserFromToken(TEST_TOKEN))
                .thenReturn(user);

    }

    /**
     * 测试1：不带 Authorization 头访问 /tasks。
     * 安全过滤器应当在 Controller 执行之前拒绝请求，返回 401。
     * 并验证 TaskService 没有被调用。
     */
    @Test
    void testWithoutToken() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(taskService);
    }

    /**
     * 测试2：模拟用户5携带 Bearer Token 查询任务列表。
     * 预先让假的 TaskService 返回一条标题为"学习MockMvc"的任务。
     */
    @Test
    void testGetTasksWithToken() throws Exception {
        prepareAuthenticatedUser();

        Task task = new Task(
                9L,
                "学习MockMvc",
                "测试Controller",
                TaskStatus.TODO,
                LocalDateTime.of(2030, 1, 1, 18, 0),
                LocalDateTime.of(2026, 10, 15, 12, 0)
        );

        // GET /tasks 没有 ?status=TODO，因此 Controller 收到的 status 是 null。
        when(taskService.getTasks(isNull(), eq(USER_ID)))
                .thenReturn(List.of(task));

        mockMvc.perform(
                        get("/tasks")
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(9))
                .andExpect(jsonPath("$[0].title").value("学习MockMvc"));

        verify(taskService).getTasks(null, USER_ID);
    }

    /**
     * 测试3：携带模拟 Token，POST JSON 创建任务。
     * 既检查 HTTP 200 / 返回 JSON，又检查 Controller 是否把参数传给 Service。
     */
    @Test
    void testCreateTaskWithToken() throws Exception {
        prepareAuthenticatedUser();

        LocalDateTime deadline = LocalDateTime.of(2030, 1, 1, 18, 0);

        Task savedTask = new Task(
                10L,
                "学习JUnit",
                "测试创建任务",
                TaskStatus.TODO,
                deadline,
                LocalDateTime.of(2026, 10, 15, 12, 0)
        );

        when(taskService.createTask(
                eq("学习JUnit"),
                eq("测试创建任务"),
                eq(deadline),
                eq(USER_ID)
        )).thenReturn(savedTask);

        mockMvc.perform(
                        post("/tasks")
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "学习JUnit",
                                          "description": "测试创建任务",
                                          "deadline": "2030-01-01T18:00:00"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("学习JUnit"));

        verify(taskService).createTask(
                "学习JUnit",
                "测试创建任务",
                deadline,
                USER_ID
        );
    }

    /**
     * 测试4：标题为空字符串。
     * CreateTaskRequest 上的 @NotBlank 与 Controller 上的 @Valid
     * 应当触发参数校验，返回 400，并且不应调用 TaskService。
     */
    @Test
    void testCreateTaskWithEmptyTitle() throws Exception {
        prepareAuthenticatedUser();

        mockMvc.perform(
                        post("/tasks")
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "",
                                          "description": "无效的创建请求",
                                          "deadline": "2030-01-01T18:00:00"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taskService);
    }
}
