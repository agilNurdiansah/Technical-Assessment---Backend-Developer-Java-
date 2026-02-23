package com.company.common.exception;

import java.time.Instant;
import java.util.List;

public class ErrorResponse {
    private String code;
    private String message;
    private String path;
    private String timestamp;
    private List<String> details;

    public ErrorResponse() {
        this.timestamp = Instant.now().toString();
    }

    public ErrorResponse(String code, String message, String path, List<String> details) {
        this.code = code;
        this.message = message;
        this.path = path;
        this.details = details;
        this.timestamp = Instant.now().toString();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}
