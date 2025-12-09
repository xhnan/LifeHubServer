package com.xhn.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * @author xhn
 * @date 2025/12/5 10:01
 * @description 统一响应封装
 */
@Setter
@Getter
public class ResponseResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 500;

    // getters & setters
    private int code;
    private String message;
    private T data;
    private long timestamp;
    private boolean success;

    public ResponseResult() {
        this.timestamp = Instant.now().toEpochMilli();
    }

    public ResponseResult(int code, String message, T data, boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
        this.timestamp = Instant.now().toEpochMilli();
    }

    // success 静态构造器
    public static <T> ResponseResult<T> success() {
        return new ResponseResult<T>(SUCCESS_CODE, "success", null, true);
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<T>(SUCCESS_CODE, "success", data, true);
    }

    public static <T> ResponseResult<T> success(String message, T data) {
        return new ResponseResult<T>(SUCCESS_CODE, message, data, true);
    }

    // error 静态构造器
    public static <T> ResponseResult<T> error() {
        return new ResponseResult<T>(ERROR_CODE, "error", null, false);
    }

    public static <T> ResponseResult<T> error(String message) {
        return new ResponseResult<T>(ERROR_CODE, message, null, false);
    }

    public static <T> ResponseResult<T> error(int code, String message) {
        return new ResponseResult<T>(code, message, null, false);
    }

    public static <T> ResponseResult<T> of(int code, String message, T data) {
        return new ResponseResult<T>(code, message, data, code == SUCCESS_CODE);
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                ", success=" + success +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseResult<?> that = (ResponseResult<?>) o;
        return code == that.code &&
                timestamp == that.timestamp &&
                success == that.success &&
                Objects.equals(message, that.message) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, data, timestamp, success);
    }
}
