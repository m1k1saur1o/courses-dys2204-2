package com.duoc.courses.service;

import java.io.ByteArrayInputStream;

import org.springframework.stereotype.Service;

import com.duoc.courses.config.S3EnrollmentProperties;
import com.duoc.courses.dto.EnrollmentS3Document;
import com.duoc.courses.exception.EnrollmentStorageException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EnrollmentStorageService {

    private final S3Template s3Template;
    private final ObjectMapper objectMapper;
    private final S3EnrollmentProperties properties;

    public EnrollmentStorageService(S3Template s3Template, ObjectMapper objectMapper, S3EnrollmentProperties properties) {
        this.s3Template = s3Template;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public void storeEnrollment(Long enrollmentId, EnrollmentS3Document document) {
        if (enrollmentId == null) {
            throw new EnrollmentStorageException("Enrollment id is required for S3 storage");
        }

        String key = "enrollments/" + enrollmentId + ".json";
        byte[] payload;
        try {
            payload = objectMapper.writeValueAsBytes(document);
        } catch (JsonProcessingException ex) {
            throw new EnrollmentStorageException("Failed to serialize enrollment payload", ex);
        }

        ObjectMetadata metadata = ObjectMetadata.builder()
                .contentType("application/json")
                .contentLength((long) payload.length)
                .build();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(payload);
        try {
            s3Template.upload(properties.getBucket(), key, inputStream, metadata);
            log.info("Enrollment stored in S3: bucket={}, key={}", properties.getBucket(), key);
        } catch (RuntimeException ex) {
            throw new EnrollmentStorageException("Failed to upload enrollment to S3", ex);
        }
    }
}
