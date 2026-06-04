package com.example.demo.repository;

import java.util.List;

import com.example.demo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByTitle(String title);

    List<Task> findByCompleted(boolean completed);
}