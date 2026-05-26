package com.duoc.courses.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duoc.courses.dto.CourseCreateRequest;
import com.duoc.courses.dto.CourseResponse;
import com.duoc.courses.entity.Course;
import com.duoc.courses.repository.CourseRepository;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll(Sort.by("id")).stream()
                .map(this::toResponse)
                .toList();
    }

    public CourseResponse createCourse(CourseCreateRequest request) {
        Course course = new Course(
                request.name(),
                request.instructor(),
                request.duration(),
                request.cost()
        );
        Course saved = courseRepository.save(course);
        return toResponse(saved);
    }

    private CourseResponse toResponse(Course course) {
        return new CourseResponse(
                course.getName(),
                course.getInstructor(),
                course.getDuration(),
                course.getCost()
        );
    }
}
