package com.dhx.template.service.impl;

import cn.hutool.core.date.DateUtil;
import com.dhx.template.config.MailConfig;
import com.dhx.template.model.enums.MailEnum;
import com.dhx.template.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author adorabled4
 * @className MessageServiceImpl
 * @date : 2023/10/21/ 13:37
 **/
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    MailConfig mailConfig;

    @Override
    public boolean sendCode(String receiver, String code, MailEnum mailEnum) {
        // 拼接内容
        String content = mailEnum.getPrefix() + code + mailEnum.getSuffix();
        // 设置信息
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailConfig.getUsername());
        message.setTo(receiver);
        message.setSubject(mailEnum.getTitle());
        message.setText(content);
        // 发送邮件
        javaMailSender.send(message);
        log.info("邮件发送成功！to：【{}】 -- 验证码：【{}】 -- {}", receiver, code, DateUtil.now());
        return true;
    }

}
