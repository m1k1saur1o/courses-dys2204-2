package com.duoc.courses.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.courses.service.EnrollmentStorageService;

@RestController
@RequestMapping("/s3")
public class S3EnrollmentController {

    private final EnrollmentStorageService enrollmentStorageService;

    public S3EnrollmentController(EnrollmentStorageService enrollmentStorageService) {
        this.enrollmentStorageService = enrollmentStorageService;
    }

    @GetMapping(value = "/enrollments/{studentId}/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> downloadSummary(@PathVariable Long studentId, @RequestParam Long summaryId) {
        String payload = enrollmentStorageService.downloadSummary(studentId, summaryId);
        return ResponseEntity.ok(payload);
    }

    @PutMapping(value = "/enrollments/{studentId}/summary", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateSummary(
            @PathVariable Long studentId,
            @RequestParam Long summaryId,
            @RequestBody String payload
    ) {
        enrollmentStorageService.updateSummary(studentId, summaryId, payload);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/enrollments/{studentId}/summary")
    public ResponseEntity<Void> deleteSummary(@PathVariable Long studentId, @RequestParam Long summaryId) {
        enrollmentStorageService.deleteSummary(studentId, summaryId);
        return ResponseEntity.noContent().build();
    }
}
