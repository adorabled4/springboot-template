package com.dhx.template.service;

import com.dhx.template.common.BaseResponse;
import com.dhx.template.model.DO.UserEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dhx.template.model.DTO.user.VerifyCodeRegisterRequest;
import com.dhx.template.model.VO.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author <a href="https://blog.dhx.icu/"> adorabled4 </a>
* @description 针对表【t_user】的数据库操作Service
* @createDate 2023-05-04 16:18:15
*/
public interface UserService extends IService<UserEntity> {

    /**
     * 删除用户id
     *
     * @param userId 用户id
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    BaseResponse<Boolean> deleteUserById(Long userId);

    /**
     * 获取用户列表
     *
     * @param pageSize 页面大小
     * @param current  当前
     * @return {@link BaseResponse}<{@link List}<{@link UserVO}>>
     */
    BaseResponse<List<UserVO>> getUserList(int pageSize, int current);

    /**
     * 得到用户id
     *
     * @param userId 用户id
     * @return {@link BaseResponse}<{@link UserVO}>
     */
    BaseResponse<UserVO> getUserById(Long userId);

    /**
     * 添加用户
     *
     * @param userVo
     * @return {@link BaseResponse}
     */
    BaseResponse addUser(UserEntity userVo);

    /**
     * 用户注册
     * @param userAccount 用户账户名
     * @param password
     * @param checkPassword
     * @return
     */
    BaseResponse register(String userAccount, String password, String checkPassword);

    /**
     * 用户登录
     * @param userAccount 账户
     * @param password 密码
     * @return 返回token
     */
    BaseResponse login(String userAccount, String password, HttpServletRequest request);

    /**
     * 是否是admin
     *
     * @param request 请求
     * @return boolean
     */
    boolean isAdmin(HttpServletRequest request);


    /**
     * 是管理
     *
     * @param user 用户
     * @return boolean
     */
    boolean isAdmin(UserEntity user);

    /**
     * 通过手机号登录(如果没有注册需要注册)
     *
     * @param phone    电话
     * @return {@link BaseResponse}
     */
    BaseResponse<String> loginByPhone(String phone,HttpServletRequest request);

    /**
     * 用户登录
     *
     * @param email 账户
     * @param password    密码
     * @return 返回token
     */
    BaseResponse login(String email, String password);

    /**
     * 通过邮箱注册
     *
     * @param request 请求
     * @return boolean
     */
    boolean register(VerifyCodeRegisterRequest request);

    /**
     * 登录
     *
     * @param email 电子邮件
     * @return {@link BaseResponse}
     */
    BaseResponse quickLogin(String email);
}