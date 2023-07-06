package com.dhx.template.utils;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.dhx.template.common.constant.SMSConstant;
import com.dhx.template.config.SmsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Random;

/**
 * @author <a href="https://blog.dhx.icu/"> adorabled4 </a>
 * @className SMSUtils
 * @date : 2023/07/05/ 17:55
 **/
@Slf4j
@Component
public class SMSUtil {

    @Resource
    Client client;

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @return boolean
     */
    public String send(String phone) {
        JSONObject param = randomCode();
        SendSmsRequest request = createRequest(phone, JSONObject.toJSONString(param));
        JSONObject jsonObject = null;
        try {
            SendSmsResponse response = client.sendSms(request);
            jsonObject = (JSONObject) JSONObject.parseObject(JSONObject.toJSONString(response)).get("body");
            System.out.println(jsonObject);
            if (jsonObject.get("code").equals(SMSConstant.SUCCESS_CODE)) {
                log.info("阿里云短信发送成功！手机号：【{}】 -- 验证码：【{}】 -- {}", phone, param.get("code"), DateUtil.now());
                return (String) param.get("code");
            }
        } catch (Exception e) {
            log.error("阿里云短信发送出现异常：{}", e.getMessage());
            return "";
        }
        log.info("阿里云短信发送失败！手机号：【{}】 -- 验证码：【{}】 -- {}", phone, param.get("code"), DateUtil.now());
        return "";
    }

    /**
     * 创建请求
     *
     * @param mobile 接受手机号
     * @return SendSmsRequest
     */
    private SendSmsRequest createRequest(String mobile, String param) {
        return new SendSmsRequest()
                .setPhoneNumbers(mobile)
                .setSignName(SMSConstant.COMMON_SIGN_NAME)//此填写签名名称
                .setTemplateCode(SMSConstant.TEMPLATE_CODE_TWO)//此填写模板CODE
                .setTemplateParam(param);//验证码参数为json字符串格式 {"code":"xxxxxx"}
    }

    /**
     * 随机6位验证码
     *
     * @return code
     */
    private JSONObject randomCode() {
        String code = RandomUtil.randomNumbers(6);
        JSONObject param = new JSONObject();
        param.put("code", code);
        return param;
    }
}
