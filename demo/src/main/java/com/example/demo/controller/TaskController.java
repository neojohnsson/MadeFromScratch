package com.example.demo.controller;

import java.util.List;

import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.dto.TaskResponse;
import com.example.demo.dto.UpdateTaskRequest;
import com.example.demo.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<TaskResponse> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public TaskResponse one(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public TaskResponse create(@Valid @RequestBody CreateTaskRequest req) {
        return service.create(req);
    }

    @PatchMapping("/{id}")
    public TaskResponse patch(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest req) {
        return service.patch(id, req);
    }

    @PatchMapping("/{id}/complete")
    public TaskResponse complete(@PathVariable Long id) {
        return service.complete(id);
    }

    @GetMapping("/completed")
    public List<TaskResponse> completed() {
        return service.findCompleted();
    }

    @GetMapping("/pending")
    public List<TaskResponse> pending() {
        return service.findPending();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }

    @GetMapping("/search")
    public List<TaskResponse> searchByTitle(@RequestParam String title) {
        return service.searchByTitle(title);
    }

    @PatchMapping("/{id}/toggle")
    public TaskResponse getComplete(@PathVariable long id) {
        return service.toggleComplete(id);
    }
}