package com.dhx.template.model.VO;

import com.dhx.template.common.constant.UserConstant;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="https://blog.dhx.icu/"> adorabled4 </a>
 * @className UserVO
 * @date : 2023/05/04/ 16:19
 **/
@Data
public class UserVO implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 昵称
     */
    @Pattern(regexp = UserConstant.USER_ACCOUNT_REGEX, message = "用户名不符合规范!")
    private String userName;

    /**
     * 账户
     */
    private String userAccount;

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
     * 用户角色
     */
    private String userRole;

    /**
     * 出生日期
     */
    private Date birth;

    private static final long serialVersionUID = 1L;

}
