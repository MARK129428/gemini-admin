package com.gemini.admin.common;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    private Long timestamp = System.currentTimeMillis();

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功响应
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "操作成功", data);
    }

    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(200, message, null);
    }

    // 失败响应
    public static <T> ApiResponse<T> failed(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return failed(401, message); // 未授权
    }

    public static <T> ApiResponse<T> forbidden(String message) {
        return failed(403, message); // 无权限
    }
}