package com.dhx.template.service;

import com.dhx.template.model.enums.MailEnum;

/**
 * @author adorabled4
 * @interface MessageService
 * @date : 2023/10/21/ 13:36
 **/
public interface MessageService {

    /**
     * 发送验证码
     *
     * @param receiver 接收机
     * @param mailEnum 邮件枚举
     * @return boolean
     */
    boolean sendCode(String receiver, String code, MailEnum mailEnum);
}
