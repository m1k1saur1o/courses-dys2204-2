package com.duoc.courses.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CourseCreateRequest(
        @NotBlank String name,
        @NotBlank String instructor,
        @NotNull @Positive Integer duration,
        @NotNull @Positive BigDecimal cost
) {
}
