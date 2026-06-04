package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Project;
import com.example.demo.repository.ProjectRepository;

@Service
public class ProjectService {

    private final ProjectRepository repo;

    public ProjectService(ProjectRepository repo) {
        this.repo = repo;
    }

    public List<Project> findAll() {
        return (List<Project>) repo.findAll();
    }

    public Project findById(Long id) {
        return repo.findById(id).
                orElseThrow(() -> new NoSuchElementException(
                        "Project with id " + id + " not found"));
    }

    public Project create(Project project) {
        if (repo.count() >= 10) {
            throw new IllegalStateException("Cannot create more than 10 projects");
        }
        return repo.save(project);
    }

    public void deleteById(Long id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Cannot delete: project with id " + id + " not found");
        }
        repo.deleteById(id);
    }
}