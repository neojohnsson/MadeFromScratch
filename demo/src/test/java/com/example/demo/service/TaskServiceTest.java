package com.example.demo.service;

import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.dto.TaskResponse;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/*
1	findById_whenTaskExists_returnsResponse	Happy path for findById
2	findById_whenTaskMissing_throwsNoSuchElement	The error path
3	create_underLimit_savesAndReturns	Happy path for create
4 COMPLETED	create_atLimit_throwsIllegalState	The 100-task rule
5	complete_whenNotCompleted_flipsAndSaves	Happy path
6	complete_whenAlreadyCompleted_throws	The double-complete rule
7	delete_whenMissing_throws	The delete error path
 */

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepo;

    @Mock
    private ProjectRepository projectRepo;

    @InjectMocks
    private TaskService service;

    @Test
    void create_atLimit_throwsIllegalState() {
        // Arrange - pretend the DP has 100 tasks
        when(taskRepo.count()).thenReturn(100L);

        CreateTaskRequest req = new CreateTaskRequest("X", "Y", 1L);

        // Act + Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(TaskService.TASK_LIMIT_MESSAGE);
    }

    @Test

}
