package com.dhx.template.model.DTO.user;

import com.dhx.template.common.constant.UserConstant;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author <a href="https://blog.dhx.icu/"> adorabled4 </a>
 * @className LoginBuPhoneReqeust
 * @date : 2023/07/05/ 16:40
 **/

@Data
public class LoginByPhoneRequest {

    /**
     * 电话
     */
    @Pattern(regexp = UserConstant.PHONE_REGEX,message = "手机号不符合规范!")
    private String phone;

    /**
     * 验证码
     */
    @NotNull(message = "验证码不能为空!")
    private String code;

}
