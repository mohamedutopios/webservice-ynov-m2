package com.foodexpress.delivery.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {

    public LocalDateTime timestamp;
    public int status;
    public String error;
    public String message;
    public String path;
    public String service;
    public List<FieldViolation> violations;

    public ErrorResponse() { this.timestamp = LocalDateTime.now(); }

    public ErrorResponse(int status, String error, String message, String path, String service) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.service = service;
    }

    public static class FieldViolation {
        public String field;
        public String message;
        public FieldViolation() {}
        public FieldViolation(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}
