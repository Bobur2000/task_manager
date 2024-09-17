package com.company.service.service;

import com.company.service.dto.TaskDto;
import com.company.service.entity.Task;

import java.util.List;

public interface TaskService {
    Task createTask(TaskDto taskDto);
    List<Task> getAllTasks();
    Task updateTask(Long id, TaskDto taskDTO);
    void deleteTask(Long id);
}
