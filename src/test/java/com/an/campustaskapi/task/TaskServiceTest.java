package com.an.campustaskapi.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Day31: test the real TaskService using fake repositories.
 * These are pure unit tests: no Spring context, MySQL or Tomcat.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldCreateTaskForAuthenticatedUser() {
        Long userId = 5L;
        User user = new User(userId, "test_user", "unused_hash");
        LocalDateTime deadline = LocalDateTime.now().plusDays(2);

        // The real Service must find the user before saving a Task.
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        // Simulate the generated database ID and return the saved entity.
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> {
                    Task task = invocation.getArgument(0);
                    task.setId(9L);
                    return task;
                });

        Task result = taskService.createTask(
                "Learn Mockito", "Service test", deadline, userId
        );

        assertNotNull(result);
        assertEquals(9L, result.getId());
        assertEquals("Learn Mockito", result.getTitle());
        assertEquals(TaskStatus.TODO, result.getStatus());
        assertSame(user, result.getUser());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldRejectPastDeadlineWithoutSaving() {
        LocalDateTime past = LocalDateTime.now().minusDays(1);

        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask("Old", "Past", past, 5L)
        );

        verifyNoInteractions(taskRepository, userRepository);
    }

    @Test
    void shouldHideTasksOwnedByOtherUsers() {
        Long taskId = 9L;
        Long otherUserId = 6L;
        when(taskRepository.findByIdAndUser_Id(taskId, otherUserId))
                .thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.getTaskById(taskId, otherUserId));

        verify(taskRepository).findByIdAndUser_Id(taskId, otherUserId);
        verify(taskRepository, never()).findById(taskId);
    }

    @Test
    void shouldQueryOnlyCurrentUsersTasks() {
        Task ownTask = new Task(9L, "My task", "Personal", TaskStatus.TODO,
                LocalDateTime.now().plusDays(1), LocalDateTime.now());
        when(taskRepository.findByUser_Id(5L)).thenReturn(List.of(ownTask));

        List<Task> result = taskService.getTasks(null, 5L);

        assertEquals(1, result.size());
        assertEquals("My task", result.get(0).getTitle());
        verify(taskRepository).findByUser_Id(5L);
    }

    @Test
    void shouldDeleteOwnedTask() {
        Task task = new Task(9L, "To delete", "", TaskStatus.TODO,
                LocalDateTime.now().plusDays(1), LocalDateTime.now());
        when(taskRepository.findByIdAndUser_Id(9L, 5L))
                .thenReturn(Optional.of(task));

        boolean deleted = taskService.deleteTask(9L, 5L);

        assertTrue(deleted);
        verify(taskRepository).delete(task);
    }
}
