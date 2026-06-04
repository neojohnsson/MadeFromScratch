package com.example.demo.controller;

import java.util.List;

import com.example.demo.entity.Task;
import com.example.demo.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<Task> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Task one(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Task create(@Valid @RequestBody Task task) {
        return service.create(task);
    }

    @PatchMapping("/{id}")
    public Task patch(@PathVariable Long id, @RequestBody Task updates) {
        return service.patch(id, updates);
    }

    @PatchMapping("/{id}/complete")
    public Task complete(@PathVariable Long id) {
        return service.complete(id);
    }

    @GetMapping("/completed")
    public List<Task> completed() {
        return service.findCompleted();
    }

    @GetMapping("/pending")
    public List<Task> pending() {
        return service.findPending();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }

    @GetMapping("/search")
    public List<Task> searchByTitle(@RequestParam String title) {
        return service.searchByTitle(title);
    }
}