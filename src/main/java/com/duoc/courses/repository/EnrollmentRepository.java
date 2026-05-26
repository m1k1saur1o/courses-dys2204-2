package com.duoc.courses.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.duoc.courses.entity.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

	@EntityGraph(attributePaths = {"student", "courses"})
	List<Enrollment> findByStudentIdOrderByCreatedAtDesc(Long studentId);
}
