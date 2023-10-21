package com.dhx.template.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dhx.template.common.BaseResponse;
import com.dhx.template.common.ErrorCode;
import com.dhx.template.common.constant.UserConstant;
import com.dhx.template.common.exception.BusinessException;
import com.dhx.template.model.DO.UserEntity;
import com.dhx.template.model.DTO.JwtToken;
import com.dhx.template.model.DTO.user.UserDTO;
import com.dhx.template.model.DTO.user.VerifyCodeRegisterRequest;
import com.dhx.template.model.VO.UserVO;
import com.dhx.template.model.enums.UserRoleEnum;
import com.dhx.template.service.JwtTokensService;
import com.dhx.template.service.UserService;
import com.dhx.template.mapper.UserMapper;
import com.dhx.template.utils.ResultUtil;
import com.dhx.template.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="https://blog.dhx.icu/"> adorabled4 </a>
 * @description 针对表【t_user】的数据库操作Service实现
 * @createDate 2023-05-04 16:18:15
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity>
        implements UserService {

    @Resource
    UserMapper userMapper;

    @Resource
    JwtTokensService jwtTokensService;

    @Override
    public BaseResponse login(String userAccount, String password, HttpServletRequest request) {
        //1. 获取的加密密码
        UserEntity user = query().eq("user_account", userAccount).one();
        String handlerPassword = user.getUserPassword();
        //2. 查询用户密码是否正确
        boolean checkpw = BCrypt.checkpw(password, handlerPassword);
        if (!checkpw) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR, "账户名或密码错误!");
        }
        //3. 获取jwt的token并将token写入Redis
        String token = jwtTokensService.generateAccessToken(user);
        String refreshToken = jwtTokensService.generateRefreshToken(user);
        JwtToken jwtToken = new JwtToken(token, refreshToken);
        jwtTokensService.save2Redis(jwtToken, user);
        //4. 保存用户的登录IPV4地址
        try {
            String remoteAddr = request.getRemoteAddr();
            if (StringUtils.isNotBlank(remoteAddr)) {
                user.setLastLoginIp(remoteAddr);
            }
        } catch (RuntimeException e) {
            log.error("保存用户登录IP失败, remoteAddress:{}", request.getRemoteAddr());
        }
        updateById(user);
        // 返回jwtToken
        return ResultUtil.success(token);
    }

    @Override
    public BaseResponse register(String userAccount, String password, String checkPassword) {
        if (!password.equals(checkPassword)) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致!");
        }
        Long cnt = query().eq("user_account", userAccount).count();
        if (cnt != 0) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR, "用户名已被注册!");
        }
        UserEntity user = new UserEntity();
        String handlerPassword = BCrypt.hashpw(password);
        user.setUserAccount(userAccount);
        user.setUserName("user-" + UUID.randomUUID().toString().substring(0, 10));
        user.setUserPassword(handlerPassword);
        boolean save = save(user);
        return ResultUtil.success(user.getUserId());
    }

    @Override
    public BaseResponse<String> loginByPhone(String phone ,HttpServletRequest request) {
        Long cnt = query().eq("phone", phone).count();
        UserEntity user;
        if (cnt == 0) {
            return ResultUtil.error(ErrorCode.NOT_FOUND_ERROR,"用户未注册!");
        } else {
            // 用户已经注册了
            user = query().eq("phone", phone).one();
            user.setPhone(phone);
        }
        // 用户登录
        String token = jwtTokensService.generateAccessToken(user);
        String refreshToken = jwtTokensService.generateRefreshToken(user);
        JwtToken jwtToken = new JwtToken(token, refreshToken);
        jwtTokensService.save2Redis(jwtToken, user);
        //4. 保存用户的登录IPV4地址
        try {
            String remoteAddr = request.getRemoteAddr();
            if (StringUtils.isNotBlank(remoteAddr)) {
                user.setLastLoginIp(remoteAddr);
            }
        } catch (RuntimeException e) {
            log.error("保存用户登录IP失败, remoteAddress:{}", request.getRemoteAddr());
        }
        updateById(user);
        // 返回jwtToken
        return ResultUtil.success(token);
    }

    /**
     * 用户登录
     *
     * @param email    账户
     * @param password 密码
     * @return 返回token
     */
    @Override
    public BaseResponse login(String email, String password) {
        try {
            //1. 获取的加密密码
            UserEntity user = query().eq("email", email).one();
            String handlerPassword = user.getUserPassword();
            //1.1 检查用户的使用状态
            if(user.getUserRole().equals(UserRoleEnum.BAN.getValue())){
                return ResultUtil.error(ErrorCode.PARAMS_ERROR, "该用户已被禁用!");
            }
            //2. 查询用户密码是否正确
            boolean checkpw = BCrypt.checkpw(password, handlerPassword);
            if (!checkpw) {
                return ResultUtil.error(ErrorCode.PARAMS_ERROR, "邮箱或密码错误!");
            }
            //3. 获取jwt的token并将token写入Redis
            String token = jwtTokensService.generateAccessToken(user);
            String refreshToken = jwtTokensService.generateRefreshToken(user);
            JwtToken jwtToken = new JwtToken(token, refreshToken);
            jwtTokensService.save2Redis(jwtToken, user);
            // 返回jwtToken
            return ResultUtil.success(token);
        } catch (RuntimeException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户未注册");
        }
    }

    /**
     * 通过邮箱注册
     *
     * @param request 请求
     * @return boolean
     */
    @Override
    public boolean register(VerifyCodeRegisterRequest request) {
        String password = request.getPassword();
        String email = request.getEmail();
        // 封装信息
        UserEntity user = new UserEntity();
        // 加密用户密码
        String handlerPassword = BCrypt.hashpw(password);
        user.setUserName("user-" + UUID.randomUUID().toString().substring(0, 4));
        user.setUserPassword(handlerPassword);
        user.setEmail(email);
        boolean save = save(user);
        return save;
    }

    /**
     * 登录
     *
     * @param email 电子邮件
     * @return {@link BaseResponse}
     */
    @Override
    public BaseResponse quickLogin(String email) {
        //1. 获取的加密密码
        List<UserEntity> users = list(new QueryWrapper<UserEntity>().eq("email", email));
        UserEntity user;
        if (users == null || users.size() == 0) {
            user = quickRegister(email);
        } else {
            user = users.get(0);
        }
        //3. 获取jwt的token并将token写入Redis
        String token = jwtTokensService.generateAccessToken(user);
        String refreshToken = jwtTokensService.generateRefreshToken(user);
        JwtToken jwtToken = new JwtToken(token, refreshToken);
        jwtTokensService.save2Redis(jwtToken, user);
        return ResultUtil.success(token);
    }

    private UserEntity quickRegister(String email) {
        // 封装信息
        UserEntity user = new UserEntity();
        // 加密用户密码
        user.setUserName("user-" + UUID.randomUUID().toString().substring(0, 4));
        user.setEmail(email);
        boolean save = save(user);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败!");
        }
        return user;
    }

    @Override
    public BaseResponse<UserVO> getUserById(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        System.out.println(user);
        if (user == null) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR, "用户不存在!");
        }
        // 转换成vo 对象
        UserVO UserVO = BeanUtil.copyProperties(user, UserVO.class);
        return ResultUtil.success(UserVO);
    }

    @Override
    public BaseResponse<Boolean> deleteUserById(Long userId) {
        boolean result = remove(new QueryWrapper<UserEntity>().eq("user_id", userId));
        return ResultUtil.success(result);
    }

    @Override
    public BaseResponse<List<UserVO>> getUserList(int pageSize, int current) {
        // 分页查询数据
        List<UserEntity> records = query().page(new Page<>(current, pageSize)).getRecords();
        // 转换为UserVO
        List<UserVO> UserVOList = records.stream().map(item -> BeanUtil.copyProperties(item, UserVO.class)).collect(Collectors.toList());
        return ResultUtil.success(UserVOList);
    }


    @Override
    public BaseResponse addUser(UserEntity user) {
        String password = user.getUserPassword();
        String handlerPassword = BCrypt.hashpw(password);
        user.setUserPassword(handlerPassword);
        save(user);
        return ResultUtil.success(user.getUserId());
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        UserDTO user = UserHolder.getUser();
        if (user.getUserRole() == null) {
            return false;
        }
        return user.getUserRole().equals(UserConstant.ADMIN_ROLE);
    }

    @Override
    public boolean isAdmin(UserEntity user) {
        if (user.getUserRole() == null) {
            return false;
        }
        return user.getUserRole().equals(UserConstant.ADMIN_ROLE);
    }
}




