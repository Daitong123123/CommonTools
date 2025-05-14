package com.daitong.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * BaseException
 *
 * @since 2025-05-14
 */
@Setter
@Getter
public class BaseException extends RuntimeException {
    private String code;
    private String msg;

    public BaseException(String code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
