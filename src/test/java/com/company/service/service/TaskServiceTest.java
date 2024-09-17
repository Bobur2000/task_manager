package com.company.service.service;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


import com.company.service.dto.TaskDto;
import com.company.service.entity.Task;
import com.company.service.entity.enums.Status;
import com.company.service.repository.TaskRepository;
import com.company.service.service.impl.TaskServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;


    // 1. Тест на создание задачи
    @Test
    public void createTask_ShouldReturnCreatedTask() {
        TaskDto taskDTO = new TaskDto("Test Task", "Test Description", LocalDate.now(), Status.OPEN);
        Task task = new Task(1L, "Test Task", "Test Description", LocalDate.now(), Status.OPEN);

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task createdTask = taskService.createTask(taskDTO);

        assertNotNull(createdTask);
        assertEquals("Test Task", createdTask.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void getAllTasks_ShouldReturnListOfTasks() {
        // Подготовка данных
        Task task1 = new Task(1L, "Task 1", "Description 1", LocalDate.now(), Status.OPEN);
        Task task2 = new Task(2L, "Task 2", "Description 2", LocalDate.now(), Status.IN_PROGRESS);
        List<Task> tasks = Arrays.asList(task1, task2);

        // Настройка мока
        when(taskRepository.findAll()).thenReturn(tasks);

        // Выполнение метода
        List<Task> result = taskService.getAllTasks();

        // Проверки
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(task1));
        assertTrue(result.contains(task2));
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    public void getAllTasks_ShouldReturnEmptyList() {
        // Настройка мока
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        // Выполнение метода
        List<Task> result = taskService.getAllTasks();

        // Проверки
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    public void updateTask_ShouldReturnUpdatedTask() {
        Long taskId = 1L;
        TaskDto taskDTO = new TaskDto("Updated Title", "Updated Description", LocalDate.now(), Status.IN_PROGRESS);
        Task existingTask = new Task(taskId, "Old Title", "Old Description", LocalDate.now().minusDays(1), Status.OPEN);
        Task updatedTask = new Task(taskId, "Updated Title", "Updated Description", LocalDate.now(), Status.IN_PROGRESS);

        // Mock the repository to return the existing task
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        // Mock the repository to return the updated task when saved
        when(taskRepository.save(existingTask)).thenReturn(updatedTask);

        // Execute the service method
        Task result = taskService.updateTask(taskId, taskDTO);

        // Verify the results
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(Status.IN_PROGRESS, result.getStatus());
        assertEquals(LocalDate.now(), result.getDueDate());

        // Verify interactions with the repository
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    public void updateTask_TaskNotFound_ShouldThrowException() {
        Long taskId = 1L;
        TaskDto taskDTO = new TaskDto("Updated Title", "Updated Description", LocalDate.now(), Status.IN_PROGRESS);

        // Mock the repository to return empty Optional
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Execute and verify exception
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            taskService.updateTask(taskId, taskDTO);
        });

        assertEquals("Task with id " + taskId + " not found", thrown.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void deleteTask_ShouldDeleteTask() {
        Long taskId = 1L;

        // Mock the repository to return true for existsById
        when(taskRepository.existsById(taskId)).thenReturn(true);

        // Execute the service method
        taskService.deleteTask(taskId);

        // Verify interactions
        verify(taskRepository, times(1)).existsById(taskId);
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    public void deleteTask_TaskNotFound_ShouldThrowException() {
        Long taskId = 1L;

        // Mock the repository to return false for existsById
        when(taskRepository.existsById(taskId)).thenReturn(false);

        // Execute and verify exception
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            taskService.deleteTask(taskId);
        });

        assertEquals("Task with id " + taskId + " not found", thrown.getMessage());
        verify(taskRepository, times(1)).existsById(taskId);
        verify(taskRepository, never()).deleteById(anyLong());
    }
}
