package com.an.campustaskapi.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;


public class UpdateTaskRequest {

    @NotBlank(message = "任务标题不能为空")
    @Size(
            max = 100,
            message = "任务标题不能超过100个字符"
    )
    private String title;


    @Size(
            max = 255,
            message = "任务描述不能超过255个字符"
    )
    private String description;


    @NotNull(message = "截止时间不能为空")
    private LocalDateTime deadline;


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public LocalDateTime getDeadline() {
        return deadline;
    }


    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
}