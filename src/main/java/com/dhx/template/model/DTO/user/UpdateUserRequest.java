package com.dhx.template.model.DTO.user;

import com.dhx.template.common.constant.UserConstant;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * @author adorabled4
 * @className UpdateUserRequest
 * @date : 2023/07/05/ 16:18
 **/
@Data
public class UpdateUserRequest implements Serializable {


    /**
     * 昵称
     */
    @Pattern(regexp = UserConstant.USER_ACCOUNT_REGEX, message = "用户名不符合规范!")
    private String userName;

    /**
     * 地址
     */
    private String address;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 性别1男0女
     */
    private Integer gender;

    /**
     * 手机号
     */
    @Pattern(regexp = UserConstant.PHONE_REGEX, message = "电话不符合规范!")
    private String phone;

    /**
     * 邮箱
     */
    @Pattern(regexp = UserConstant.EMAIL_REGEX, message = "邮箱不符合规范!")
    private String email;

    /**
     * 出生日期
     */
    private Date birth;

    private static final long serialVersionUID = 1L;

}
