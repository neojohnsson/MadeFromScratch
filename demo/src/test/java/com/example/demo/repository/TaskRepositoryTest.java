package com.example.demo.repository;

import java.util.List;

import com.example.demo.entity.Task;
import com.example.demo.entity.Project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {
    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    private ProjectRepository projectRepo;

    private Project defaultProject;

    @BeforeEach
    void setUpProject() {
        defaultProject = new Project();
        defaultProject.setName("Test project");
        projectRepo.save(defaultProject);
    }

    @Test
    void findByCompleted_whenSomeAreDone_returnsOnlyDone() {
        // Arrange - create one of each
        Task completed = new Task();
        completed.setTitle("Done thing");
        completed.setCompleted(true);
        completed.setProject(defaultProject); // <- Attach the project
        taskRepo.save(completed);

        Task pending = new Task();
        pending.setTitle("Not yet");
        pending.setCompleted(false);
        pending.setProject(defaultProject);
        taskRepo.save(pending);

        // Act
        List<Task> result = taskRepo.findByCompleted(true);

        // Assert
        assertThat(result)
                .hasSize(1)
                .extracting(Task::getTitle)
                .containsExactly("Done thing");
    }

    @Test
    void findByTitle_whenTitleMatches_returnsMatchingTask() {
        // Arrange - create 2 tasks with different title
        Task correctTitle = new Task();
        correctTitle.setTitle("Correct title");
        correctTitle.setCompleted(false);          // ← explicit
        correctTitle.setProject(defaultProject);
        taskRepo.save(correctTitle);

        Task wrongTitle = new Task();
        wrongTitle.setTitle("Wrong title");
        wrongTitle.setCompleted(false);          // ← explicit
        wrongTitle.setProject(defaultProject);
        taskRepo.save(wrongTitle);

        // Act
        List<Task> result = taskRepo.findByTitle("Correct title");

        // Assert
        assertThat(result)
                .hasSize(1)
                .extracting(Task::getTitle)
                .containsExactly("Correct title");
    }

    @Test
    void findByProjectId_whenMultipleProjects_returnsOnlyTasksFromGivenProject() {
        // Arrange
        Project projectA = new Project();
        projectA.setName("Test project 1");
        projectRepo.save(projectA);

        Project projectB = new Project();
        projectB.setName("Test project 2");
        projectRepo.save(projectB);

        Task taskInA1 = new Task();
        taskInA1.setTitle("A's first task");
        taskInA1.setProject(projectA);
        taskRepo.save(taskInA1);

        Task taskInA2 = new Task();
        taskInA2.setTitle("A's second task");
        taskInA2.setProject(projectA);
        taskRepo.save(taskInA2);

        Task taskInB1 = new Task();
        taskInB1.setTitle("B's only task");
        taskInB1.setProject(projectB);
        taskRepo.save(taskInB1);

        // Act
        List<Task> result = taskRepo.findByProjectId(projectA.getId());

        // Assert
        assertThat(result)
                .hasSize(2)
                .extracting(Task::getTitle)
                .containsExactlyInAnyOrder("A's first task", "A's second task");
    }
}