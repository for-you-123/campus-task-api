package com.an.campustaskapi.task;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    // 1. 查询当前用户的任务列表，保留状态筛选。
    @GetMapping
    public List<Task> getTasks(
            @RequestParam(required = false) TaskStatus status,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        return service.getTasks(status, currentUser.id());
    }

    // 2. 查询当前用户的任务分页。
    @GetMapping("/page")
    public Page<Task> getTaskPage(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        return service.getTaskPage(status, page, size, currentUser.id());
    }

    // 3. 查询当前用户自己的任务。
    @GetMapping("/{id}")
    public Task getTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        return service.getTaskById(id, currentUser.id());
    }

    // 4. 创建属于当前用户的任务，不接收客户端指定的 userId。
    @PostMapping
    public Task createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        return service.createTask(
                request.getTitle(),
                request.getDescription(),
                request.getDeadline(),
                currentUser.id()
        );
    }

    // 5. 修改当前用户自己的任务。
    @PutMapping("/{id}")
    public Task updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        return service.updateTask(
                id,
                request.getTitle(),
                request.getDescription(),
                request.getDeadline(),
                currentUser.id()
        );
    }

    // 6. 修改当前用户自己的任务状态。
    @PatchMapping("/{id}/status")
    public Task updateTaskStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskStatusRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        return service.updateTaskStatus(
                id,
                request.getStatus(),
                currentUser.id()
        );
    }

    // 7. 删除当前用户自己的任务。
    @DeleteMapping("/{id}")
    public boolean deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        return service.deleteTask(id, currentUser.id());
    }
}
