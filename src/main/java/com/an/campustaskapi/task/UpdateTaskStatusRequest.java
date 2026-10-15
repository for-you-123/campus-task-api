package com.an.campustaskapi.task;

import jakarta.validation.constraints.NotNull;


public class UpdateTaskStatusRequest {


    @NotNull(message = "任务状态不能为空")
    private TaskStatus status;


    public TaskStatus getStatus() {
        return status;
    }


    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}