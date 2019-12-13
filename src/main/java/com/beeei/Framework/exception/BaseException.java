package com.beeei.Framework.exception;

import java.io.Serializable;

public class BaseException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 341039033989383101L;

    private String code;

    private String message;

    public BaseException(String code,String message) {
        super(message, null, false, false);
        this.code = code;
        this.message = message;
    }

    public BaseException(BaseErrorCodeEnum error) {
        super(error.getMessage(), null, false, false);
        this.code = error.getErrorCode();
        this.message = error.getMessage();
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


}
