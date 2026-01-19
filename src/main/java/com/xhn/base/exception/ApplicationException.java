package com.xhn.base.exception;

import lombok.Getter;

/**
 * @author xhn
 * @date 2026/1/4 14:58
 * @description
 */
@Getter
public class ApplicationException extends RuntimeException{
    private final Integer code;

    private final String  message;

    public ApplicationException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ApplicationException(String message){
        super(message);
        this.code = 500;
        this.message = message;
    }


}
