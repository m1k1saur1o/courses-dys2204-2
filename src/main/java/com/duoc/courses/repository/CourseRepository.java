package com.duoc.courses.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duoc.courses.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
