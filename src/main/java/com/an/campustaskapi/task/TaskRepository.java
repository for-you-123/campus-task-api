package com.an.campustaskapi.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository
        extends JpaRepository<Task, Long> {

    // 原有功能：按状态查询任务
    List<Task> findByStatus(
            TaskStatus status
    );

    // 原有功能：按状态分页查询
    Page<Task> findByStatus(
            TaskStatus status,
            Pageable pageable
    );

    // Day28：查询当前用户的全部任务
    List<Task> findByUser_Id(
            Long userId
    );

    // Day28：查询当前用户指定状态的任务
    List<Task> findByUser_IdAndStatus(
            Long userId,
            TaskStatus status
    );

    // Day28：对当前用户的任务进行分页
    Page<Task> findByUser_Id(
            Long userId,
            Pageable pageable
    );

    // Day28：按用户、状态进行分页
    Page<Task> findByUser_IdAndStatus(
            Long userId,
            TaskStatus status,
            Pageable pageable
    );

    // Day28：按任务ID和所属用户ID查询
    Optional<Task> findByIdAndUser_Id(
            Long id,
            Long userId
    );
}