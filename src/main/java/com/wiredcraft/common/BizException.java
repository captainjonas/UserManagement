package com.wiredcraft.common;


import lombok.Data;

/**
 * @author Eric Yao
 * @date 2022-11-06
 */
@Data
public class BizException extends RuntimeException{

    private String errorCode;

    public BizException(String errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(ErrCodeEnum errorCode) {
        super(errorCode.getErrorMsg());
        this.errorCode = errorCode.getErrorCode();
    }

    public String getErrorCode() {
        return errorCode;
    }

}
