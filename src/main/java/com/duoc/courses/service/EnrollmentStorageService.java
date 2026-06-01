package com.duoc.courses.service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.duoc.courses.config.S3EnrollmentProperties;
import com.duoc.courses.dto.EnrollmentS3Document;
import com.duoc.courses.exception.BadRequestException;
import com.duoc.courses.exception.EnrollmentStorageException;
import com.duoc.courses.exception.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class EnrollmentStorageService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentStorageService.class);
    private static final String ENROLLMENTS_PREFIX = "enrollments";
    private static final String JSON_CONTENT_TYPE = "application/json";

    private final S3Template s3Template;
    private final ObjectMapper objectMapper;
    private final S3EnrollmentProperties properties;
    private final S3Client s3Client;

    public EnrollmentStorageService(
            S3Template s3Template,
            ObjectMapper objectMapper,
            S3EnrollmentProperties properties,
            S3Client s3Client
    ) {
        this.s3Template = s3Template;
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.s3Client = s3Client;
    }

    public void storeEnrollment(Long studentId, Long summaryId, EnrollmentS3Document document) {
        validateIds(studentId, summaryId);

        String key = buildKey(studentId, summaryId);
        byte[] payload;
        try {
            payload = objectMapper.writeValueAsBytes(document);
        } catch (JsonProcessingException ex) {
            throw new EnrollmentStorageException("Failed to serialize enrollment payload", ex);
        }

        uploadPayload(key, payload);
    }

    public String downloadSummary(Long studentId, Long summaryId) {
        validateIds(studentId, summaryId);

        String key = buildKey(studentId, summaryId);
        if (!objectExists(key)) {
            throw new NotFoundException("Enrollment summary not found: " + key);
        }

        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(key)
                    .build();
            ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(request);
            return responseBytes.asUtf8String();
        } catch (NoSuchKeyException ex) {
            throw new NotFoundException("Enrollment summary not found: " + key);
        } catch (S3Exception ex) {
            throw new EnrollmentStorageException("Failed to download enrollment summary from S3", ex);
        }
    }

    public void updateSummary(Long studentId, Long summaryId, String jsonPayload) {
        validateIds(studentId, summaryId);
        validateJsonPayload(jsonPayload);

        String key = buildKey(studentId, summaryId);
        if (!objectExists(key)) {
            throw new NotFoundException("Enrollment summary not found: " + key);
        }

        byte[] payloadBytes = jsonPayload.getBytes(StandardCharsets.UTF_8);
        uploadPayload(key, payloadBytes);
    }

    public void deleteSummary(Long studentId, Long summaryId) {
        validateIds(studentId, summaryId);

        String key = buildKey(studentId, summaryId);
        if (!objectExists(key)) {
            throw new NotFoundException("Enrollment summary not found: " + key);
        }

        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(key)
                    .build();
            s3Client.deleteObject(request);
        } catch (S3Exception ex) {
            throw new EnrollmentStorageException("Failed to delete enrollment summary from S3", ex);
        }
    }

    private void uploadPayload(String key, byte[] payload) {
        ObjectMetadata metadata = ObjectMetadata.builder()
                .contentType(JSON_CONTENT_TYPE)
                .contentLength((long) payload.length)
                .build();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(payload);
        try {
            s3Template.upload(properties.getBucket(), key, inputStream, metadata);
            log.info("Enrollment summary stored in S3: bucket={}, key={}", properties.getBucket(), key);
        } catch (RuntimeException ex) {
            throw new EnrollmentStorageException("Failed to upload enrollment to S3", ex);
        }
    }

    private boolean objectExists(String key) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(key)
                    .build();
            s3Client.headObject(request);
            return true;
        } catch (NoSuchKeyException ex) {
            return false;
        } catch (S3Exception ex) {
            if (ex.statusCode() == 404) {
                return false;
            }
            throw new EnrollmentStorageException("Failed to check S3 object existence", ex);
        }
    }

    private void validateIds(Long studentId, Long summaryId) {
        if (studentId == null || studentId <= 0) {
            throw new BadRequestException("studentId must be a positive number");
        }

        if (summaryId == null || summaryId <= 0) {
            throw new BadRequestException("summaryId must be a positive number");
        }
    }

    private void validateJsonPayload(String jsonPayload) {
        if (jsonPayload == null || jsonPayload.isBlank()) {
            throw new BadRequestException("JSON payload is required");
        }

        try {
            objectMapper.readTree(jsonPayload);
        } catch (JsonProcessingException ex) {
            throw new BadRequestException("Invalid JSON payload");
        }
    }

    private String buildKey(Long studentId, Long summaryId) {
        return ENROLLMENTS_PREFIX + "/" + studentId + "/" + summaryId + ".json";
    }
}
