package com.example.demo.service;

import com.example.demo.entity.Project;
import com.example.demo.entity.Task;

import com.example.demo.dto.TaskResponse;
import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.dto.UpdateTaskRequest;

import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;

import com.example.demo.service.TaskService;

import java.util.Optional;
import java.util.NoSuchElementException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



/*
    Method	                    Happy	Error
    findById	                ✅   	✅ 	Fully covered
    create	                    ✅   	✅  Limit case covered. Project-not-found case not tested.
    complete	                ✅  	✅  Already-completed covered. Task-not-found case not tested.
    deleteById	                ✅	    ✅  Not tested
    findAll	                    ✅	    ❌	Not tested - skip
    patch	                    ✅	    ✅	Not tested
    searchByTitle	            ❌	    ❌	Not tested - skip
    findCompleted	            ❌	    ❌	Not tested - skip
    findPending	                ❌	    ❌	Not tested - skip
    findByProjectId (service)	❌	    ❌	Not tested
 */

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepo;

    @Mock
    private ProjectRepository projectRepo;

    @InjectMocks
    private TaskService service;

    // Happy path for toggleComplete (false -> true)
    @Test
    void toggleComplete_whenToggleToTrue_returnTask() {
        Task task = new Task();
        task.setTitle("Buy paint");
        task.setCompleted(false);

        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepo.save(task)).thenReturn(task);

        TaskResponse result = service.toggleComplete(1L);

        assertThat(result.completed()).isTrue();
    }

    // Happy path for toggleComplete (true -> false)
    @Test
    void toggleComplete_whenToggleToFalse_returnTask() {
        Task task = new Task();
        task.setTitle("Buy paint");
        task.setCompleted(true);

        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepo.save(task)).thenReturn(task);

        TaskResponse result = service.toggleComplete(1L);

        assertThat(result.completed()).isFalse();
    }

    // Error path for toggleComplete
    @Test
    void toggleComplete_whenTaskMissing_throws() {
        when(taskRepo.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.toggleComplete(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Cannot toggle task: task with id 1 not found");
    }

    // Happy path for findByProjectId
    @Test
    void findByProjectId_whenProjectExists_returnsTasks() {
        // Arrange
        Task task1 = new Task();
        task1.setTitle("Buy paint");
        task1.setCompleted(false);

        Task task2 = new Task();
        task2.setTitle("Walk dog");
        task2.setCompleted(false);

        when(projectRepo.existsById(1L)).thenReturn(true); // returns boolean
        when(taskRepo.findByProjectId(1L)).thenReturn(List.of(task1, task2)); // returns List<Task>

        // Act
        List<TaskResponse> result = service.findByProjectId(1L);

        // Assert
        assertThat(result)
                .hasSize(2)
                .extracting(TaskResponse::title)
                .containsExactly("Buy paint", "Walk dog");
    }

    // Error path for findByProjectId
    @Test
    void findByProjectId_whenProjectMissing_throws() {
        // Arrange
        when(projectRepo.existsById(1L)).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> service.findByProjectId(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Cannot find tasks: project with id 1 not found");

    }

    // Happy path for patch
    @Test
    void patch_whenUpdated_returnsNewResponse() {
        // Arrange
        Project project = new Project();
        project.setName("Home");

        Task task = new Task();
        task.setTitle("Buy paint");
        task.setDescription("Today");
        task.setCompleted(false);
        task.setProject(project);

        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepo.save(task)).thenReturn(task);

        // The patch request - change title and description, leave project alone
        UpdateTaskRequest req = new UpdateTaskRequest("Walk dog", "Tomorrow", null);

        // Act
        TaskResponse result = service.patch(1L, req);

        // Assert
        assertThat(result.title()).isEqualTo("Walk dog");
        assertThat(result.description()).isEqualTo("Tomorrow");
        assertThat(result.completed()).isFalse();
    }

    // Error path for patchProject
    @Test
    void patch_whenProjectMissing_throws() {
        // Arrange
        Task task = new Task();
        task.setTitle("Buy paint");
        task.setDescription("Today");
        task.setCompleted(false);

        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        when(projectRepo.findById(1L)).thenReturn(Optional.empty());

        // create the new project
        UpdateTaskRequest req = new UpdateTaskRequest("Walk dog", "Tomorrow", 1L);

        // Act + Assert
        assertThatThrownBy(() -> service.patch(1L, req))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Project with id 1 not found");
    }

    // Error path for patchTask
    @Test
    void patch_whenTaskMissing_throws() {
        // Arrange
        when(taskRepo.findById(1L)).thenReturn(Optional.empty());

        // The patch request - change title and description, leave project alone
        UpdateTaskRequest req = new UpdateTaskRequest("Walk dog", "Tomorrow", null);

        // Act + Assert
        assertThatThrownBy(() -> service.patch(1L, req))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Cannot update task: task with id 1 not found");
    }

    // Happy path for findAll
    @Test
    void findAll_whenTasksExist_returnsListOfResponses() {
        // Arrange, create 2 tasks
        Task task1 = new Task();
        task1.setTitle("Buy paint");
        task1.setCompleted(false);

        Task task2 = new Task();
        task2.setTitle("Walk dog");
        task2.setCompleted(false);

        when(taskRepo.findAll()).thenReturn(List.of(task1, task2));

        // Act
        List<TaskResponse> result = service.findAll();

        // Assert
        assertThat(result)
                .hasSize(2)
                .extracting(TaskResponse::title)
                .containsExactly("Buy paint", "Walk dog");
    }

    // Happy path for deleteById
    @Test
    void delete_whenTaskExists_deletesSuccessfully() {
        // Arrange - task exists in the DB
        when(taskRepo.existsById(1L)).thenReturn(true);

        // Act
        service.deleteById(1L);

        // Assert - the repo's delete was actually called
        verify(taskRepo).deleteById(1L);
    }

    // Error path for deleteById
    @Test
    void delete_whenMissing_throws() {
        // Arrange - task does NOT exists in the DB
        when(taskRepo.existsById(1L)).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> service.deleteById(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Cannot delete: task with id 1 not found");
    }

    // Happy path for complete
    @Test
    void complete_whenNotCompleted_flipsAndSaves() {
        // Arrange - task exists, NOT yet completed
        Task task = new Task();
        task.setTitle("Buy paint");
        task.setCompleted(false); // <- key: not completed yet

        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepo.save(task)).thenReturn(task); // returns the same task

        // Act
        TaskResponse result = service.complete(1L);

        // Assert
        assertThat(result.completed()).isTrue();
        assertThat(result.title()).isEqualTo("Buy paint");
    }

    // Error path for complete when task missing
    @Test
    void complete_whenTaskMissing_throws() {
        // Arrange
        when(taskRepo.findById(1L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> service.complete(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Cannot complete task: task with id 1 not found");
    }

    // Error path for complete
    @Test
    void complete_whenAlreadyCompleted_throws() {
        // Arrange - task exists, ALREADY completed
        Task task = new Task();
        task.setTitle("Buy paint");
        task.setCompleted(true); // <- key: is completed

        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));

        // Act + Assert
        assertThatThrownBy(() -> service.complete(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot complete task: task already completed");
    }

    // Happy path for create
    @Test
    void create_underLimit_saveAndReturns() {
        // Arrange
        when(taskRepo.count()).thenReturn(10L); // Under the limit (Stub 1)

        // Project that will be linked to the task
        Project project = new Project();
        project.setName("Home");
        when(projectRepo.findById(1L)).thenReturn(Optional.of(project)); // Stub 2

        // What the repo "returns" after save - simulating hibernate filling things in
        Task savedTask = new Task();
        savedTask.setTitle("Buy paint");
        savedTask.setDescription("Latex");
        savedTask.setCompleted(false);
        savedTask.setProject(project);
        when(taskRepo.save(any(Task.class))).thenReturn(savedTask); // Stub 3

        // The DTO the client would send
        CreateTaskRequest req = new CreateTaskRequest("Buy paint", "Latex", 1L);

        // Act
        TaskResponse result = service.create(req);

        // Asserts
        assertThat(result.title()).isEqualTo("Buy paint");
        assertThat(result.description()).isEqualTo("Latex");
        assertThat(result.completed()).isFalse();
    }

    // Error path for create project not found
    @Test
    void create_whenProjectMissing_throws() {
        // Arrange
        when(projectRepo.findById(1L)).thenReturn(Optional.empty());

        CreateTaskRequest req = new CreateTaskRequest("placeholder", "placeholder", 1L);

        // Act + Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Project with id 1 not found");
    }

    // Error path for create
    @Test
    void create_atLimit_throwsIllegalState() {
        // Arrange - pretend the DP has 100 tasks
        when(taskRepo.count()).thenReturn(100L);

        CreateTaskRequest req = new CreateTaskRequest("placeholder", "placeholder", 1L);

        // Act + Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(TaskService.TASK_LIMIT_MESSAGE);
    }

    // Happy path for findById
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

    // Error path for findById
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
