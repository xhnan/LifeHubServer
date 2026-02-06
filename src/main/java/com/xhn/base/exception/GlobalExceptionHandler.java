package com.xhn.base.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.xhn.response.ResponseResult;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理系统中的各种异常，返回标准的响应格式
 *
 * @author xhn
 * @date 2026/1/4 15:00
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 处理业务异常
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ResponseResult<Void>> handleBusinessException(ApplicationException e) {
        logger.error("业务异常: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body(ResponseResult.error(e.getCode(), e.getMessage()));
    }

    /**
     * 处理参数校验异常（WebFlux）
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ResponseResult<Void>> handleWebExchangeBindException(WebExchangeBindException e) {
        logger.warn("参数校验失败: {}", e.getMessage());
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseResult.error(400, "参数校验失败: " + message));
    }

    /**
     * 处理参数校验异常（传统MVC）
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ResponseResult<Void>> handleValidException(Exception e) {
        logger.warn("参数校验异常: {}", e.getMessage());
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

    /**
     * 处理JSON字段类型转换异常
     */
    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ResponseResult<Void>> handleInvalidFormat(InvalidFormatException ex) {
        logger.warn("字段类型转换异常: 字段={}, 期望类型={}, 实际值={}",
                ex.getPath().get(0).getFieldName(), ex.getTargetType().getSimpleName(), ex.getValue());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseResult.error(400, "字段类型不匹配，期望类型: " +
                        ex.getTargetType().getSimpleName() + ", 实际值: " + ex.getValue()));
    }

    /**
     * 处理请求参数错误异常
     */
    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ResponseResult<Void>> handleServerWebInputException(
            ServerWebInputException e,
            ServerWebExchange exchange
    ) {
        Throwable cause = e.getCause();
        String requestPath = exchange.getRequest().getPath().value();
        String requestMethod = exchange.getRequest().getMethod().name();
        String queryParams = exchange.getRequest().getURI().getQuery();
        String contentType = exchange.getRequest().getHeaders().getContentType() != null
                ? exchange.getRequest().getHeaders().getContentType().toString()
                : "unknown";

        logger.warn("========================================");
        logger.warn("请求参数错误详情:");
        logger.warn("请求路径: {} {}", requestMethod, requestPath);
        logger.warn("Content-Type: {}", contentType);
        if (queryParams != null && !queryParams.isEmpty()) {
            logger.warn("查询参数: {}", queryParams);
        }

        // 打印请求头信息
        logger.warn("请求头: {}", exchange.getRequest().getHeaders().toSingleValueMap());

        logger.warn("异常原因: {}", e.getReason());
        logger.warn("异常消息: {}", e.getMessage());
        logger.warn("完整异常: ", e);
        logger.warn("========================================");

        if (cause instanceof DecodingException) {
            Throwable rootCause = cause.getCause();

            if (rootCause instanceof MismatchedInputException) {
                MismatchedInputException mie = (MismatchedInputException) rootCause;
                String fieldName = mie.getPath().stream()
                        .map(JsonMappingException.Reference::getFieldName)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse("unknown");

                logger.warn("字段类型不匹配 - 字段: {}, 期望类型: {}",
                        fieldName, mie.getTargetType().getSimpleName());

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
     * 处理 405 方法不允许异常
     */
    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<ResponseResult<Void>> handleMethodNotAllowed(MethodNotAllowedException e) {
        logger.warn("请求方法不允许: {}", e.getHttpMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ResponseResult.error(405, "请求方法不允许: " + e.getHttpMethod()));
    }

    /**
     * 处理 415 不支持的媒体类型异常
     */
    @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
    public ResponseEntity<ResponseResult<Void>> handleUnsupportedMediaType(UnsupportedMediaTypeStatusException e) {
        logger.warn("不支持的媒体类型: {}", e.getContentType());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ResponseResult.error(415, "不支持的媒体类型"));
    }

    /**
     * 处理 406 不可接受异常
     */
    @ExceptionHandler(NotAcceptableStatusException.class)
    public ResponseEntity<ResponseResult<Void>> handleNotAcceptable(NotAcceptableStatusException e) {
        logger.warn("不可接受的响应类型: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(ResponseResult.error(406, "不可接受的响应类型"));
    }

    /**
     * 处理通用的ResponseStatusException
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ResponseResult<Void>> handleResponseStatusException(ResponseStatusException e) {
        logger.warn("响应状态异常: {}", e.getReason());
        return ResponseEntity.status(e.getStatusCode())
                .body(ResponseResult.error(e.getStatusCode().value(), e.getReason()));
    }

    /**
     * 处理JWT Token过期异常
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ResponseResult<Void>> handleExpiredJwtException(ExpiredJwtException e) {
        logger.warn("Token已过期: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseResult.error(401, "Token已过期，请重新登录"));
    }

    /**
     * 处理JWT签名异常
     */
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ResponseResult<Void>> handleSignatureException(SignatureException e) {
        logger.error("Token签名验证失败: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseResult.error(401, "Token无效"));
    }

    /**
     * 处理JWT其他异常
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ResponseResult<Void>> handleJwtException(JwtException e) {
        logger.error("JWT异常: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseResult.error(401, "Token无效或已过期"));
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseResult<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("非法参数: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseResult.error(400, e.getMessage()));
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
     * 注意：这个处理器应该放在最后，作为兜底
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseResult<Void>> handleException(Exception e) {
        logger.error("系统异常: {}", e.getClass().getSimpleName(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseResult.error(500, "系统异常,请联系管理员"));
    }

}
