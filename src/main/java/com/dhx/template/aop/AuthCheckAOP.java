package com.dhx.template.aop;

import com.dhx.template.common.ErrorCode;
import com.dhx.template.common.annotation.AuthCheck;
import com.dhx.template.common.exception.BusinessException;
import com.dhx.template.model.DTO.user.UserDTO;
import com.dhx.template.model.enums.UserRoleEnum;
import com.dhx.template.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author <a href="https://blog.dhx.icu/"> adorabled4 </a>
 * @className AuthCheckAOP
 * @date : 2023/05/04/ 16:26
 **/
@Aspect
@Component
@Slf4j
public class AuthCheckAOP {

    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.dhx.template.common.annotation.AuthCheck)")
    public void logPointcut() {
    }

    @Around(value = "logPointcut()")
    public Object run(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();//方法签名
        Method method = ((MethodSignature) signature).getMethod();
        //这个方法才是目标对象上有注解的方法
        Method realMethod = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(), method.getParameterTypes());
        //获取注解
        AuthCheck authCheck = realMethod.getAnnotation(AuthCheck.class);
        String mustRole = authCheck.mustRole();
        if (StringUtils.isNotBlank(mustRole)) {
            UserDTO loginUser = UserHolder.getUser();
            UserRoleEnum mustUserRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
            if (mustUserRoleEnum == null) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            String userRole = loginUser.getUserRole();
            // 如果被封号，直接拒绝
            if (UserRoleEnum.BAN.equals(mustUserRoleEnum)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            // 必须有管理员权限
            if (UserRoleEnum.ADMIN.equals(mustUserRoleEnum)) {
                if (!mustRole.equals(userRole)) {
                    throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
                }
            }
        }
        return joinPoint.proceed();
    }


}
