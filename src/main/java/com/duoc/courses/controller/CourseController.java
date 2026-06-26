package com.duoc.courses.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.duoc.courses.dto.CourseCreateRequest;
import com.duoc.courses.dto.CourseResponse;
import com.duoc.courses.service.CourseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<CourseResponse> getAllCourses() {
        return courseService.getAllCourses();
    }

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseCreateRequest request) {
        CourseResponse response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
