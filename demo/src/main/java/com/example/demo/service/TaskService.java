package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.dto.TaskResponse;
import com.example.demo.dto.UpdateTaskRequest;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;

@Service
public class TaskService {

    public static final String TASK_LIMIT_MESSAGE = "Cannot create more than 100 tasks";

    private final TaskRepository taskRepo;
    private final ProjectRepository projectRepo;

    public TaskService(TaskRepository taskRepo, ProjectRepository projectRepo) {
        this.taskRepo = taskRepo;
        this.projectRepo = projectRepo;
    }

    public List<TaskResponse> findAll() {
        return taskRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public TaskResponse findById(Long id) {
        Task t = taskRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Task with id " + id + " not found"));
        return toResponse(t);
    }

    public TaskResponse create(CreateTaskRequest req) {
        if (taskRepo.count() >= 100) {
            throw new IllegalStateException(TASK_LIMIT_MESSAGE);
        }
        Task entity = toEntity(req);
        Task saved = taskRepo.save(entity);
        return toResponse(saved);
    }

    public TaskResponse complete(Long id) {
        Task task = taskRepo.findById(id).
                orElseThrow(() -> new NoSuchElementException(
                        "Cannot complete task: task with id " + id + " not found"));
        if (task.getCompleted() == true) {
            throw new IllegalStateException("Cannot complete task: task already completed");
        }
        task.setCompleted(true);
        return toResponse(taskRepo.save(task));
    }

    public TaskResponse patch(Long id, UpdateTaskRequest req) {
        Task existing = taskRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Cannot update task: task with id " + id + " not found"));
        if (req.title() != null) {
            existing.setTitle(req.title());
        }
        if (req.description() != null) {
            existing.setDescription(req.description());
        }
        if (req.projectId() != null) {
            Project p = projectRepo.findById(req.projectId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Project with id " + req.projectId() + " not found"));
            existing.setProject(p);
        }
        return toResponse(taskRepo.save(existing));
    }

    public void deleteById(Long id) {
        if (!taskRepo.existsById(id)) {
            throw new NoSuchElementException("Cannot delete: task with id " + id + " not found");
        }
        taskRepo.deleteById(id);
    }

    public List<TaskResponse> searchByTitle(String title) {
        return taskRepo.findByTitle(title).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TaskResponse> findCompleted() {
        return taskRepo.findByCompleted(true).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TaskResponse> findPending() {
        return taskRepo.findByCompleted(false).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TaskResponse> findByProjectId(Long projectID) {
        if (!projectRepo.existsById(projectID)) {
            throw new NoSuchElementException(
                    "Cannot find tasks: project with id " + projectID + " not found");
        }

        return taskRepo.findByProjectId(projectID).stream()
                .map(this::toResponse)
                .toList();
    }

    // Helpers

    private Task toEntity(CreateTaskRequest req) {
        Task t = new Task();
        t.setTitle(req.title());
        t.setDescription(req.description());
        if (req.projectId() != null) {
            Project p = projectRepo.findById(req.projectId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Project with id " + req.projectId() + " not found"));
            t.setProject(p);
        }
        return t;
    }

    private TaskResponse toResponse(Task t) {
        return new TaskResponse(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getCompleted(),
                t.getProject() != null ? t.getProject().getId() : null
        );
    }
}