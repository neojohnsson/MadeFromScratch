package com.example.demo.controller;

import java.util.List;

import com.example.demo.dto.CreateProjectRequest;
import com.example.demo.dto.ProjectResponse;
import com.example.demo.entity.Task;
import com.example.demo.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
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
    public List<Task> allTasks(@PathVariable Long id) {
        // Needs to be implemented
        throw new UnsupportedOperationException("This endpoint is not yet implemented");
    }

    @PostMapping
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest project) {
        return service.create(project);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
