package com.example.demo.controller;

import java.util.List;

import com.example.demo.dto.CreateProjectRequest;
import com.example.demo.dto.ProjectResponse;
import com.example.demo.dto.TaskResponse;
import com.example.demo.service.ProjectService;
import com.example.demo.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService service;
    private final TaskService taskService;

    public ProjectController(ProjectService service, TaskService taskService) {
        this.service = service;
        this.taskService = taskService;
    }

    @GetMapping
    public List<ProjectResponse> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ProjectResponse one(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/{id}/tasks")
    public List<TaskResponse> allTasks(@PathVariable Long id) {
        return taskService.findByProjectId(id);
    }

    @PostMapping
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest req) {
        return service.create(req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
