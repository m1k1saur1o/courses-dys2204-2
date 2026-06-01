package com.duoc.courses.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record EnrollmentS3Document(
        Long enrollmentId,
        StudentResponse student,
        List<CourseSummaryResponse> courses,
        BigDecimal totalCost,
        Instant createdAt
) {
}
