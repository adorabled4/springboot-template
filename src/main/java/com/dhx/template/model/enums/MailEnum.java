package com.dhx.template.model.enums;

/**
 * @author adorabled4
 * @className MainEnum
 * @date : 2023/10/21/ 13:36
 **/
public enum MailEnum {

    /**
     * 验证码
     */
    VERIFY_CODE("[hxBI]您的验证码为: ", "请确保信息安全, 不要泄露您的验证码", "[hxBI]验证码");

    /**
     * 前缀
     */
    private String prefix;
    /**
     * 后缀
     */
    private String suffix;
    /**
     * 标题
     */
    private String title;

    MailEnum(String prefix, String suffix, String title) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.title = title;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getTitle() {
        return title;
    }
}
