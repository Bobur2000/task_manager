package com.company.service.controller;

import com.company.service.dto.TaskDto;
import com.company.service.entity.Task;
import com.company.service.entity.enums.Status;
import com.company.service.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createTask_ShouldReturnCreatedTask() throws Exception {
        TaskDto taskDTO = new TaskDto("New Task", "Description", LocalDate.now(), Status.OPEN);
        Task task = new Task(1L, "New Task", "Description", LocalDate.now(), Status.OPEN);

        when(taskService.createTask(any(TaskDto.class))).thenReturn(task);

        mockMvc.perform(post("/tasks/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    public void getAllTasks_ShouldReturnTaskList() throws Exception {
        Task task1 = new Task(1L, "Task 1", "Description 1", LocalDate.now(), Status.OPEN);
        Task task2 = new Task(2L, "Task 2", "Description 2", LocalDate.now(), Status.OPEN);
        List<Task> tasks = Arrays.asList(task1, task2);

        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/tasks/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"));
    }

    @Test
    public void updateTask_ShouldReturnUpdatedTask() throws Exception {
        Long taskId = 1L;
        TaskDto taskDTO = new TaskDto("Updated Task", "Updated Description", LocalDate.now(), Status.IN_PROGRESS);
        Task updatedTask = new Task(taskId, "Updated Task", "Updated Description", LocalDate.now(), Status.IN_PROGRESS);

        when(taskService.updateTask(eq(taskId), any(TaskDto.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/tasks/update/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    public void deleteTask_ShouldReturnNoContent() throws Exception {
        Long taskId = 1L;

        doNothing().when(taskService).deleteTask(taskId);

        mockMvc.perform(delete("/tasks/delete/{id}", taskId))
                .andExpect(status().isNoContent());
    }
}


