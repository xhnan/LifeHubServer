package com.xhn.base.exception;

import com.xhn.response.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
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
    public ResponseResult<Void> handleBusinessException(ApplicationException e) {
        logger.error("业务异常: {}", e.getMessage(), e);
        return ResponseResult.error(e.getCode(), e.getMessage());
    }


    /**
     * 处理参数校验异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseResult<Void> handleValidException(Exception e) {
        logger.error("参数校验异常: {}", e.getMessage(), e);
        String message = "参数校验失败";
        if (e instanceof MethodArgumentNotValidException ex) {
            if (ex.getBindingResult().hasErrors()) {
                message = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
            }
        }
        return ResponseResult.error(400, message);
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseResult<Void> handleNullPointerException(NullPointerException e) {
        logger.error("空指针异常", e);
        return ResponseResult.error("系统异常,请联系管理员");
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseResult<Void> handleException(Exception e) {
        logger.error("系统异常", e);
        return ResponseResult.error("系统异常,请联系管理员");
    }


}
