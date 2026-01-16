package com.example.bankcards.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Date;

@Builder
public record ErrorMessage(
        @Schema(description = "HTTP статус код", example = "500")
        int statusCode,
        @Schema(description = "Временная метка ошибки", example = "2025-11-20T10:30:00.123Z")
        Date timestamp,
        @Schema(description = "Сообщение об ошибке", example = "An unexpected error occurred")
        String message,
        @Schema(description = "Путь, на котором произошла ошибка", example = "/workouts/signin")
        String description
    ) {
}
