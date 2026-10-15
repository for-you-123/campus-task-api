package com.an.campustaskapi.task;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;


@Entity
@Table(name = "tasks")
public class Task {


    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;


    private String title;


    private String description;


    @Enumerated(EnumType.STRING)
    private TaskStatus status;


    private LocalDateTime deadline;


    private LocalDateTime createdAt;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    public Task() {

    }


    public Task(
            Long id,
            String title,
            String description,
            TaskStatus status,
            LocalDateTime deadline,
            LocalDateTime createdAt
    ) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.deadline = deadline;
        this.createdAt = createdAt;
    }


    public Long getId() {

        return id;
    }


    public void setId(Long id) {

        this.id = id;
    }


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


    public TaskStatus getStatus() {

        return status;
    }


    public void setStatus(TaskStatus status) {

        this.status = status;
    }


    public LocalDateTime getDeadline() {

        return deadline;
    }


    public void setDeadline(LocalDateTime deadline) {

        this.deadline = deadline;
    }


    public LocalDateTime getCreatedAt() {

        return createdAt;
    }


    public void setCreatedAt(
            LocalDateTime createdAt
    ) {

        this.createdAt = createdAt;
    }


    public User getUser() {

        return user;
    }


    public void setUser(User user) {

        this.user = user;
    }
}