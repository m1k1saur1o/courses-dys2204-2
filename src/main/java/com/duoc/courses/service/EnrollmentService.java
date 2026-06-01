package com.duoc.courses.service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duoc.courses.dto.CourseSummaryResponse;
import com.duoc.courses.dto.EnrollmentRequest;
import com.duoc.courses.dto.EnrollmentResponse;
import com.duoc.courses.dto.EnrollmentS3Document;
import com.duoc.courses.dto.StudentResponse;
import com.duoc.courses.entity.Course;
import com.duoc.courses.entity.Enrollment;
import com.duoc.courses.entity.Student;
import com.duoc.courses.exception.BadRequestException;
import com.duoc.courses.exception.NotFoundException;
import com.duoc.courses.repository.CourseRepository;
import com.duoc.courses.repository.EnrollmentRepository;
import com.duoc.courses.repository.StudentRepository;

@Service
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentStorageService enrollmentStorageService;

    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            EnrollmentStorageService enrollmentStorageService
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentStorageService = enrollmentStorageService;
    }

    public EnrollmentResponse createEnrollment(EnrollmentRequest request) {
        if (request.courseIds() == null || request.courseIds().isEmpty()) {
            throw new BadRequestException("courseIds must not be empty");
        }

        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new NotFoundException("Student not found: " + request.studentId()));

        List<Course> courses = courseRepository.findAllById(request.courseIds());
        validateAllCoursesFound(request.courseIds(), courses);

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourses(new LinkedHashSet<>(courses));
        Enrollment saved = enrollmentRepository.save(enrollment);

        EnrollmentS3Document document = new EnrollmentS3Document(
            saved.getId(),
            new StudentResponse(student.getId(), student.getName(), student.getEmail()),
            courses.stream().map(this::toCourseSummary).toList(),
            saved.getTotalCost(),
            saved.getCreatedAt()
        );
        enrollmentStorageService.storeEnrollment(saved.getId(), document);

        return new EnrollmentResponse(
                new StudentResponse(student.getId(), student.getName(), student.getEmail()),
                courses.stream().map(this::toCourseSummary).toList(),
                saved.getTotalCost(),
                saved.getCreatedAt()
        );
    }

        @Transactional(readOnly = true)
        public List<EnrollmentResponse> getEnrollmentsForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));

        StudentResponse studentResponse = new StudentResponse(
            student.getId(),
            student.getName(),
            student.getEmail()
        );

        return enrollmentRepository.findByStudentIdOrderByCreatedAtDesc(studentId).stream()
            .map(enrollment -> new EnrollmentResponse(
                studentResponse,
                enrollment.getCourses().stream()
                    .sorted(Comparator.comparing(Course::getId))
                    .map(this::toCourseSummary)
                    .toList(),
                enrollment.getTotalCost(),
                enrollment.getCreatedAt()
            ))
            .toList();
        }

    private void validateAllCoursesFound(List<Long> requestedIds, List<Course> courses) {
        Set<Long> missing = new LinkedHashSet<>(requestedIds);
        for (Course course : courses) {
            missing.remove(course.getId());
        }
        if (!missing.isEmpty()) {
            String message = "Courses not found: " + missing.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            throw new NotFoundException(message);
        }
    }

    private CourseSummaryResponse toCourseSummary(Course course) {
        return new CourseSummaryResponse(
                course.getId(),
                course.getName(),
                course.getInstructor(),
                course.getDuration(),
                course.getCost()
        );
    }
}
