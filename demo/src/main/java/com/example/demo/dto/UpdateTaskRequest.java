package com.example.demo.dto;

import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(
        @Size(max = 100)
        String title,

        @Size(max = 500)
        String description,

        Long projectId
) {
}