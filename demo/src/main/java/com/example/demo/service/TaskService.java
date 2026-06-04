package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Task;
import com.example.demo.repository.TaskRepository;

@Service
public class TaskService {

    private final TaskRepository repo;

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    public List<Task> findAll() {
        return (List<Task>) repo.findAll();
    }

    public Task findById(Long id) {
        return repo.findById(id).
                orElseThrow(() -> new NoSuchElementException(
                        "Task with id " + id + " not found"));
    }

    public Task create(Task task) {
        if (task.getProject() == null || task.getProject().getId() == null) {
            throw new IllegalStateException("Task must belong to a project");
        }
        if (repo.count() >= 100) {
            throw new IllegalStateException("Cannot create more than 100 tasks");
        }
        return repo.save(task);
    }

    public Task complete(Long id) {
        Task task = repo.findById(id).
                orElseThrow(() -> new NoSuchElementException(
                        "Cannot complete task: task with id " + id + " not found"));
        if (task.getCompleted() == true) {
            throw new IllegalStateException("Cannot complete task: task already completed");
        }
        task.setCompleted(true);
        return repo.save(task);
    }

    public Task patch(Long id, Task updates) {
        Task existing = repo.findById(id).
                orElseThrow(() -> new NoSuchElementException(
                        "Cannot update task: task with id " + id + " not found"));
        if (updates.getTitle() != null) {
            existing.setTitle(updates.getTitle());
        }
        if (updates.getDescription() != null) {
            existing.setDescription(updates.getDescription());
        }
        if (updates.getProject() != null) {
            existing.setProject(updates.getProject());
        }
        return repo.save(existing);
    }

    public void deleteById(Long id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Cannot delete: task with id " + id + " not found");
        }
        repo.deleteById(id);
    }

    public List<Task> searchByTitle(String title) {
        return repo.findByTitle(title);
    }

    public List<Task> findCompleted() {
        return repo.findByCompleted(true);
    }

    public List<Task> findPending() {
        return repo.findByCompleted(false);
    }
}