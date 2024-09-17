package com.company.service.repository;

import com.company.service.entity.Task;
import com.company.service.entity.enums.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void testSaveTask() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setStatus(Status.OPEN);

        Task savedTask = taskRepository.save(task);
        assertNotNull(savedTask.getId());
    }
}

