package com.dhx.template.common.exception;

import com.dhx.template.common.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://blog.dhx.icu/"> adorabled4 </a>
 * @className BusinessException
 * @date : 2023/01/07/ 14:28
 **/
@Data
public class BusinessException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 165474231423634L;

    /**
     * 错误码
     */
    private int code=50000;


    /**
     * 错误描述
     */
    private String message;

    public BusinessException(int code ,String message){
        super(message);// 错误信息
        this.code=code;
        this.message=message;
    }

    public BusinessException(ErrorCode errorCode, String message){
        super(errorCode.getMsg());// 错误信息
        this.code=errorCode.getCode();
        this.message=message;
    }

    public BusinessException(ErrorCode errorCode){
        super(errorCode.getMsg());// 错误信息
        this.code=errorCode.getCode();
        this.message=errorCode.getMsg();
    }
}
