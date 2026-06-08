package com.example.demo.service;

import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.dto.TaskResponse;

import java.util.Optional;
import java.util.NoSuchElementException;

import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/*
1	COMPLETED findById_whenTaskExists_returnsResponse	Happy path for findById
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
    void findById_whenTaskExists_returnsResponse() {
        // Arrange - build a fake Task and program the mock to return it
        Project project = new Project();
        project.setName("Home");

        Task task = new Task();
        task.setTitle("Buy paint");
        task.setDescription("Latex");
        task.setCompleted(false);
        task.setProject(project);

        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));

        // Act - call the service
        TaskResponse result = service.findById(1L);

        // Assert - verify the response's fields
        assertThat(result.title()).isEqualTo("Buy paint");
        assertThat(result.description()).isEqualTo("Latex");
        assertThat(result.completed()).isFalse();
    }

    @Test
    void findById_whenTaskMissing_throwsNoSuchElement() {
        // Arrange - mock returns empty (simulating "not in DB")
        when(taskRepo.findById(1L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Task with id 1 not found");
    }
}
