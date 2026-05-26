package com.duoc.courses.dto;

import java.math.BigDecimal;

public record CourseSummaryResponse(
        Long id,
        String name,
        String instructor,
        Integer duration,
        BigDecimal cost
) {
}
