package com.xhn.base.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.xhn.response.ResponseResult;
import io.jsonwebtoken.io.DecodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebInputException;

import java.util.Objects;

/**
 * @author xhn
 * @date 2026/1/4 15:00
 * @description
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ResponseResult<Void>> handleBusinessException(ApplicationException e) {
        logger.error("业务异常: {}", e.getMessage(), e);
        return  ResponseEntity.internalServerError().body(ResponseResult.error(e.getCode(), e.getMessage()));
    }



    /**
     * 处理参数校验异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ResponseResult<Void>> handleValidException(Exception e) {
        logger.error("参数校验异常: {}", e.getMessage(), e);
        String message = "参数校验失败";
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            if (ex.getBindingResult().hasErrors()) {
                message = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseResult.error(400, message));
    }


    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ResponseResult<Void>> handleInvalidFormat(InvalidFormatException ex) {
        logger.error("字段类型转换异常: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseResult.error(400, "字段类型不匹配，期望类型: " +
                        ex.getTargetType().getSimpleName() + ", 实际值: " + ex.getValue()));
    }


    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ResponseResult<Void>> handleServerWebInputException(ServerWebInputException e) {
        Throwable cause = e.getCause();
        logger.error("请求参数错误: {}", e.getMessage(), e);
        if (cause instanceof DecodingException) {
            Throwable rootCause = cause.getCause();

            if (rootCause instanceof MismatchedInputException) {
                MismatchedInputException mie = (MismatchedInputException) rootCause;
                String fieldName = mie.getPath().stream()
                        .map(JsonMappingException.Reference::getFieldName)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse("unknown");

                return ResponseEntity.badRequest().body(
                        ResponseResult.error("字段 '" + fieldName + "' 的类型不匹配，期望类型: " +
                                mie.getTargetType().getSimpleName())
                );
            }
        }

        return ResponseEntity.badRequest().body(
                ResponseResult.error("请求参数错误: " + e.getReason())
        );
    }

    /**
     * 处理 404 资源未找到异常
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseResult<Void>> handleNoResourceFound(NoResourceFoundException e) {
        logger.warn("资源未找到: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseResult.error(404, "请求的资源不存在"));
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ResponseResult<Void>> handleNullPointerException(NullPointerException e) {
        logger.error("空指针异常", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseResult.error(500, "系统异常,请联系管理员"));
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseResult<Void>> handleException(Exception e) {
        logger.error("系统异常", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseResult.error(500, "系统异常,请联系管理员"));
    }




}
