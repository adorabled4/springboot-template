package com.dhx.template.config;

import com.dhx.template.aop.ReFreshTokenInterceptor;
import com.dhx.template.service.JwtTokensService;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author <a href="https://blog.dhx.icu/"> adorabled4 </a>
 * @className MvcConfig
 * @date : 2023/01/07/ 14:55
 **/
@Configuration
public class MvcConfig implements WebMvcConfigurer {


    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    JwtTokensService jwtTokensService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注意不要拦截knife4j的接口文档
        registry.addInterceptor(new ReFreshTokenInterceptor(stringRedisTemplate, jwtTokensService)).addPathPatterns("/**")
                .excludePathPatterns(
                        "/**/login/**",
                        "/**/register/**",
                        "/**/doc.html/**",
                        "/static/**",
                        "/**/swagger-ui.html/**",
                        "/**/favicon.ico",
                        "/**/swagger-resources/**",
                        "/**/webjars/**"
                );
        WebMvcConfigurer.super.addInterceptors(registry);
    }


    /**
     * 添加全局跨域配置
     *
     * @param registry 注册表
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求
        registry.addMapping("/**")
                // 允许发送 Cookie
                .allowCredentials(true)
                // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }

}
