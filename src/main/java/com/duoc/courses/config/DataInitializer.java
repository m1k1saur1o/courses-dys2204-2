package com.duoc.courses.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.duoc.courses.entity.Course;
import com.duoc.courses.entity.Student;
import com.duoc.courses.repository.CourseRepository;
import com.duoc.courses.repository.StudentRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public DataInitializer(CourseRepository courseRepository, StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public void run(String... args) {
        if (studentRepository.count() == 0) {
            studentRepository.saveAll(List.of(
                    new Student("Maria Diaz", "maria.diaz@example.com"),
                    new Student("Javier Silva", "javier.silva@example.com")
            ));
        }

        if (courseRepository.count() == 0) {
            courseRepository.saveAll(List.of(
                    new Course("Spring Boot Basics", "Ana Torres", 24, new BigDecimal("199.00")),
                    new Course("REST API Design", "Luis Gomez", 18, new BigDecimal("149.00")),
                    new Course("JPA and Hibernate", "Sofia Rojas", 30, new BigDecimal("249.00"))
            ));
        }
    }
}
