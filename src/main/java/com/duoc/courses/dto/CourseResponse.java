package com.duoc.courses.dto;

import java.math.BigDecimal;

public record CourseResponse(
        String name,
        String instructor,
        Integer duration,
        BigDecimal cost
) {
}
