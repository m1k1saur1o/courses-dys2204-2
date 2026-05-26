package com.duoc.courses.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EnrollmentRequest(
        @NotNull @Positive Long studentId,
        @NotEmpty List<@Positive Long> courseIds
) {
}
