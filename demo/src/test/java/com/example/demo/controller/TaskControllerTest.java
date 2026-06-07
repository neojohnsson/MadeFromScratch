package com.example.demo.controller;

import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.dto.TaskResponse;
import com.example.demo.service.TaskService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.NoSuchElementException;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private TaskService taskService;

    @Test
    void createTask_returnsCreatedTask() throws Exception {
        // Arrange
        CreateTaskRequest req = new CreateTaskRequest("Buy paint", "Latex", 1L);
        TaskResponse fakeResponse = new TaskResponse(42L, "Buy paint", "Latex", false, 1L);
        when(taskService.create(any(CreateTaskRequest.class))).thenReturn(fakeResponse);

        // Act + Assert
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.title").value("Buy paint"));
    }

    @Test
    void createTask_emptyTitle_returns400() throws Exception {
        // Arrange - Invalid body
        CreateTaskRequest req = new CreateTaskRequest("", "x", 1L);

        // Act + Assert
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTask_missingId_returns404() throws Exception {
        // Arrange - Invalid path / id
        when(taskService.findById(9999L))
                .thenThrow(new NoSuchElementException("Task with id 9999 not found"));

        // Act + Assert
        mockMvc.perform(get("/api/tasks/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_completedTrue() throws Exception {
        // Arrange - when the service is asked to complete task 42, return a "completed" task
        TaskResponse completedTask = new TaskResponse(42L, "Buy paint", "Latex", true, 1L);
        when(taskService.complete(42L)).thenReturn(completedTask);

        // Act + Assert
        mockMvc.perform(patch("/api/tasks/42/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.completed").value(true));
    }
}