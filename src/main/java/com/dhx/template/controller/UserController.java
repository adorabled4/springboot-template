package com.dhx.template.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.dhx.template.common.BaseResponse;
import com.dhx.template.common.ErrorCode;
import com.dhx.template.common.annotation.AuthCheck;
import com.dhx.template.common.annotation.SysLog;
import com.dhx.template.common.constant.RedisConstant;
import com.dhx.template.common.constant.UserConstant;
import com.dhx.template.model.DO.UserEntity;
import com.dhx.template.model.DTO.user.*;
import com.dhx.template.model.VO.UserVO;
import com.dhx.template.service.JwtTokensService;
import com.dhx.template.service.UserService;
import com.dhx.template.utils.ResultUtil;
import com.dhx.template.utils.SMSUtil;
import com.dhx.template.utils.ThrowUtil;
import com.dhx.template.utils.UserHolder;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author adorabled4
 * @className UserController
 * @date : 2023/05/04/ 16:41
 **/
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    UserService userService;

    @Resource
    JwtTokensService jwtTokensService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    SMSUtil smsUtil;


    @PostMapping("/login")
    @ApiOperation("用户登录")
    @SysLog("用户登录")
    public BaseResponse login(@Valid @RequestBody LoginRequest param, HttpServletRequest request) {
        if (param == null) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = param.getUserAccount();
        String password = param.getPassword();
        if (password == null || userAccount == null) {
            return ResultUtil.error(ErrorCode.NULL_ERROR);
        }
        return userService.login(userAccount, password, request);
    }

    @PostMapping("/login/phone")
    @ApiOperation("手机号登录")
    @SysLog
    public BaseResponse<String> loginByPhone(@RequestBody @Valid LoginByPhoneRequest param, HttpServletRequest request) {
        ThrowUtil.throwIf(StringUtils.isBlank(param.getCode()) || param.getCode().length() != UserConstant.CODE_LEN,
                ErrorCode.PARAMS_ERROR, "验证码非法!");
        String codeKey = RedisConstant.CODE_KEY + param.getPhone();
        String codeVal = stringRedisTemplate.opsForValue().get(codeKey);
        // 验证码是否过期
        if(StringUtils.isBlank(codeVal)){
            return  ResultUtil.error(ErrorCode.FORBIDDEN_ERROR,"验证码过期, 请重新发送!");
        }
        String[] split = codeVal.split("-");
        long time = Long.parseLong(split[0]);
        if(new Date().getTime()/1000 - time > 60 * 5){
            return  ResultUtil.error(ErrorCode.FORBIDDEN_ERROR,"验证码过期, 请重新发送!");
        }
        // 校验code
        String code = split[1];
        if (code.equals(param.getCode())) {
            return userService.loginByPhone(param.getPhone(), request);
        }
        return ResultUtil.error(ErrorCode.PARAMS_ERROR, "验证码错误!");
    }

    @PostMapping("/login/code")
    @ApiOperation("发送手机登录验证码")
    @SysLog
    public BaseResponse sendCodeSMS(@RequestParam("phone") String phone) {
        String codeKey = RedisConstant.CODE_KEY + phone;
        String oldCode = stringRedisTemplate.opsForValue().get(codeKey);
        // 判断是否之前发送过
        if(StringUtils.isNotBlank(oldCode)){
            String[] split = oldCode.split("-");
            long time = Long.parseLong(split[0]);
            // 如果两次发送的间隔小于 60s => reject
            if(new Date().getTime()/1000 - time < 60){
                return  ResultUtil.error(ErrorCode.FORBIDDEN_ERROR,"请稍后发送验证码!");
            }
        }
        // TODO OSS Email
        // 拼接时间戳
        String newCode = smsUtil.send(phone);
        if(newCode.equals("")){
            return ResultUtil.error(ErrorCode.SYSTEM_ERROR,"发送失败,服务异常!");
        }
        long now = new Date().getTime()/1000;
        String codeVal = now+"-"+newCode;
        // 存储
        stringRedisTemplate.opsForValue().set(codeKey, codeVal, RedisConstant.CODE_TTL, TimeUnit.SECONDS);
        return ResultUtil.success();
    }

    @GetMapping("/logout")
    @ApiOperation("退出登录")
    public BaseResponse logOut(HttpServletRequest request) {
        UserDTO user = UserHolder.getUser();
        jwtTokensService.removeTokenByUserId(user.getUserId());
        return ResultUtil.success();
    }

    @PostMapping("/register")
    @ApiOperation("用户注册")
    public BaseResponse register(@Valid @RequestBody RegisterRequest param) {
        if (param == null) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = param.getUserAccount();
        String password = param.getPassword();
        ;
        String checkPassword = param.getCheckPassword();
        ;
        return userService.register(userAccount, password, checkPassword);
    }

    @GetMapping("/{id}")
    @ApiOperation("通过用户id获取用户信息")
    public BaseResponse<UserVO> getUserById(@PathVariable("id") Long userId) {
        if (userId == null || userId < 0) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        return userService.getUserById(userId);
    }


    @GetMapping("/list")
    @ApiOperation("获取用户列表")
    public BaseResponse<List<UserVO>> getUserList(
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
            @RequestParam(value = "current", defaultValue = "1") int current) {
        return userService.getUserList(pageSize, current);
    }


    @DeleteMapping("/{id}")
    @ApiOperation("通过ID删除用户")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserById(@PathVariable("id") Long userId) {
        if (userId == null || userId < 0) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        return userService.deleteUserById(userId);
    }


    @PostMapping("/add")
    @ApiOperation("添加用户")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse addUser(@RequestBody @Valid UserEntity userVO) {
        if (userVO == null) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        return userService.addUser(userVO);
    }

    @PostMapping("/update")
    @ApiOperation("更新用户信息")
    public BaseResponse updateUserInfo(@RequestBody @Valid UpdateUserRequest userVO) {
        if (userVO == null) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        UserDTO user = UserHolder.getUser();
        UserEntity userEntity = BeanUtil.copyProperties(userVO, UserEntity.class);
        userEntity.setUserId(user.getUserId());
        return ResultUtil.success(userService.updateById(userEntity));
    }

    @GetMapping("/current")
    @ApiOperation("获取当前用户信息")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserVO> currentUser() {
        UserDTO user = UserHolder.getUser();
        UserEntity userEntity = userService.getById(user.getUserId());
        UserVO UserVO = BeanUtil.copyProperties(userEntity, UserVO.class);
        return ResultUtil.success(UserVO);
    }
}
