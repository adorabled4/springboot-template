package com.dhx.template.utils;

import com.dhx.template.model.DTO.user.UserDTO;

/**
 * @author <a href="https://blog.dhx.icu/"> adorabled4 </a>
 * @className UserHolder
 * @date : 2023/05/04/ 16:13
 **/
public class UserHolder {
    public static ThreadLocal<UserDTO> user = new ThreadLocal<>();

    public static UserDTO getUser() {
        return user.get();
    }

    public static void setUser(UserDTO userDTO) {
        user.set(userDTO);
    }
}
