package com.duoc.courses.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.courses.dto.EnrollmentResponse;
import com.duoc.courses.service.EnrollmentService;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final EnrollmentService enrollmentService;

    public StudentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/{id}/enrollments")
    public List<EnrollmentResponse> getEnrollmentsForStudent(@PathVariable("id") Long id) {
        return enrollmentService.getEnrollmentsForStudent(id);
    }
}
