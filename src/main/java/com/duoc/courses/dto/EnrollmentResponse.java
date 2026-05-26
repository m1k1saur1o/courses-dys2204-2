package com.duoc.courses.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record EnrollmentResponse(
        StudentResponse student,
        List<CourseSummaryResponse> courses,
        BigDecimal totalCost,
        Instant createdAt
) {
}
