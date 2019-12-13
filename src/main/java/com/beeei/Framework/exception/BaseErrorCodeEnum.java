package com.beeei.Framework.exception;

import java.util.Objects;

public enum  BaseErrorCodeEnum {

    SYSTEM_ERROR_MESSAGE("00000","服务繁忙，请稍后！"),
    SYSTEM_ERROR_MESSAGE_SERVICE("00001","=============   后台报错!!!   ===========>>>   \n请求地址："),
    SYSTEM_ERROR_MESSAGE_SERVICE_PARAMETER("00002","\n请求参数："),
    SYSTEM_ERROR_MESSAGE_SERVICE_BODY("00003","\n请求体数据："),
    SYSTEM_ERROR_PARAMETER_NAME_TIPS("00004","参数异常->应填参数名:"),
    SYSTEM_ERROR_PARAMETER_TYPE_TIPS("00005","  应填参数类型:"),
    SYSTEM_ERROR_TABLE_NAME("00006","表名错误"),





    passwordAndAccountError("100001","账号或密码错误"),
    accountExist("100002","系统账号已存在"),
    validateCodeNotExist("100003","验证码无效"),
    systemErrorLoginLose("100004","登录失效"),
    labelNameExistError("100005","标签名已存在"),
    tcode("100006","推广人号码无效"),
    gameAccountExist("100007","游戏账号已存在"),
    gameAccountMax("100008","免费游戏账号数量达到上限"),
    wrongfulString("100009","非法字符"),
    gameAccountNotExist("100010","游戏账号不存在"),
    gameAccountAndPassword("100011","游戏账号密码不符"),
            ;

    String errorCode;
    String message;

    BaseErrorCodeEnum(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static String getMessageByCode(String Code) {
		for (BaseErrorCodeEnum s : BaseErrorCodeEnum.values()) {
			if (Objects.equals(s.getErrorCode(), Code)) {
				return s.getMessage();
			}
		}
		return "";
	}

	public static String getErrorCodeByMessage(String message) {
		for (BaseErrorCodeEnum s : BaseErrorCodeEnum.values()) {
			if (Objects.equals(message, s.getMessage())) {
				return s.getErrorCode();
			}
		}
		return "";
	}
    
    public BaseException toException() {
        return new BaseException(this.getErrorCode(),this.getMessage());
    }
}
