package com.helpuni.color_run_backend.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class HttpResponse<T> {
    private LocalDateTime timestamp;
    private int statusCode;
    private String message;
    private T data;
    public HttpResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public HttpResponse(int statusCode, String message, T data) {
        this.timestamp = LocalDateTime.now();
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public static <T> HttpResponse<T> of(int statusCode, String message, T data) {
        return new HttpResponse<>(statusCode, message, data);
    }
}
