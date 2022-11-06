package com.wiredcraft.common;


import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author Eric Yao
 * @date 2022-11-06
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrCodeEnum {

    ILLEGAL_ARGUMENTS("RE060615012001", "参数错误"),
    USER_NOT_FOUND("RE060615012002", "用户不存在"),
    ;

    private String errorCode;
    private String errorMsg;

    ErrCodeEnum(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
