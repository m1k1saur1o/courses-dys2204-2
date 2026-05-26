package com.duoc.courses.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duoc.courses.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
