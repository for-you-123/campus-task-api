package com.an.campustaskapi.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);


    private final TaskRepository repository;

    private final UserRepository userRepository;

    public TaskService(
            TaskRepository repository,
            UserRepository userRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    // 1. 创建当前登录用户的任务
    public Task createTask(
            String title,
            String description,
            LocalDateTime deadline,
            Long currentUserId
    ) {

        log.info(
                "开始创建任务，title={}, userId={}",
                title,
                currentUserId
        );

        LocalDateTime now = LocalDateTime.now();

        if (deadline.isBefore(now)) {

            log.warn(
                    "任务创建失败，截止时间早于当前时间"
            );

            throw new IllegalArgumentException(
                    "截止时间不能早于当前时间"
            );
        }

        User user = userRepository
                .findById(currentUserId)
                .orElseThrow(
                        () -> new InvalidTokenException()
                );

        Task task = new Task(
                null,
                title,
                description,
                TaskStatus.TODO,
                deadline,
                now
        );

        task.setUser(user);

        Task savedTask = repository.save(task);

        log.info(
                "任务创建成功，taskId={}, userId={}",
                savedTask.getId(),
                currentUserId
        );

        return savedTask;
    }

    // 2. 查询当前用户的任务，可按状态筛选
    public List<Task> getTasks(
            TaskStatus status,
            Long currentUserId
    ) {

        log.info(
                "查询任务，userId={}, status={}",
                currentUserId,
                status
        );

        if (status == null) {

            return repository.findByUser_Id(
                    currentUserId
            );
        }

        return repository.findByUser_IdAndStatus(
                currentUserId,
                status
        );
    }

    // 3. 查询当前用户的任务分页
    public Page<Task> getTaskPage(
            TaskStatus status,
            int page,
            int size,
            Long currentUserId
    ) {

        log.info(
                "分页查询任务，userId={}, status={}, page={}, size={}",
                currentUserId,
                status,
                page,
                size
        );

        if (page < 0) {

            log.warn(
                    "分页参数错误，page={}",
                    page
            );

            throw new IllegalArgumentException(
                    "页码不能小于0"
            );
        }

        if (size < 1 || size > 100) {

            log.warn(
                    "分页参数错误，size={}",
                    size
            );

            throw new IllegalArgumentException(
                    "每页数量必须在1到100之间"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.ASC,
                        "id"
                )
        );

        if (status == null) {

            return repository.findByUser_Id(
                    currentUserId,
                    pageable
            );
        }

        return repository.findByUser_IdAndStatus(
                currentUserId,
                status,
                pageable
        );
    }

    // 4. 查询当前用户自己的某个任务
    public Task getTaskById(
            Long id,
            Long currentUserId
    ) {

        log.debug(
                "查询任务详情，taskId={}, userId={}",
                id,
                currentUserId
        );

        return repository
                .findByIdAndUser_Id(
                        id,
                        currentUserId
                )
                .orElseThrow(
                        () -> new TaskNotFoundException(id)
                );
    }

    // 5. 修改当前用户自己的任务
    public Task updateTask(
            Long id,
            String title,
            String description,
            LocalDateTime deadline,
            Long currentUserId
    ) {

        log.info(
                "开始修改任务，taskId={}, userId={}",
                id,
                currentUserId
        );

        Task task = getTaskById(
                id,
                currentUserId
        );

        if (deadline.isBefore(LocalDateTime.now())) {

            log.warn(
                    "修改任务失败，截止时间非法，taskId={}",
                    id
            );

            throw new IllegalArgumentException(
                    "截止时间不能早于当前时间"
            );
        }

        task.setTitle(title);
        task.setDescription(description);
        task.setDeadline(deadline);

        Task updatedTask = repository.save(task);

        log.info(
                "任务修改成功，taskId={}, userId={}",
                id,
                currentUserId
        );

        return updatedTask;
    }

    // 6. 修改当前用户自己的任务状态
    public Task updateTaskStatus(
            Long id,
            TaskStatus status,
            Long currentUserId
    ) {

        log.info(
                "修改任务状态，taskId={}, status={}, userId={}",
                id,
                status,
                currentUserId
        );

        Task task = getTaskById(
                id,
                currentUserId
        );

        task.setStatus(status);

        Task updatedTask = repository.save(task);

        log.info(
                "任务状态修改成功，taskId={}, status={}",
                updatedTask.getId(),
                updatedTask.getStatus()
        );

        return updatedTask;
    }

    // 7. 删除当前用户自己的任务
    @Transactional
    public boolean deleteTask(
            Long id,
            Long currentUserId
    ) {

        log.info(
                "开始删除任务，taskId={}, userId={}",
                id,
                currentUserId
        );

        Task task = getTaskById(
                id,
                currentUserId
        );

        repository.delete(task);

        log.info(
                "任务删除成功，taskId={}, userId={}",
                id,
                currentUserId
        );

        return true;
    }
}