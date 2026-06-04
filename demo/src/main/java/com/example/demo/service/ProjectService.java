package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.example.demo.dto.CreateProjectRequest;
import com.example.demo.dto.ProjectResponse;
import com.example.demo.entity.Project;
import com.example.demo.repository.ProjectRepository;

@Service
public class ProjectService {

    private final ProjectRepository repo;

    public ProjectService(ProjectRepository repo) {
        this.repo = repo;
    }

    public List<ProjectResponse> findAll() {
        return repo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProjectResponse findById(Long id) {
        Project p = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Project with id " + id + " not found"));
        return toResponse(p);
    }

    public ProjectResponse create(CreateProjectRequest req) {
        if (repo.count() >= 10) {
            throw new IllegalStateException("Cannot create more than 10 projects");
        }
        Project entity = toEntity(req);
        Project saved = repo.save(entity);
        return toResponse(saved);
    }

    public void deleteById(Long id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Cannot delete: project with id " + id + " not found");
        }
        repo.deleteById(id);
    }

    // Helpers

    private Project toEntity(CreateProjectRequest req) {
        Project p = new Project();
        p.setName(req.name());
        return p;
    }

    private ProjectResponse toResponse(Project p) {
        return new ProjectResponse(p.getId(), p.getName());
    }
}