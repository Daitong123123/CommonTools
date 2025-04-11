package com.daitong.manager;

import com.daitong.bo.common.UserInfo;
import org.springframework.stereotype.Service;

@Service
public class UserManager {
    public static final ThreadLocal<UserInfo> USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void setCurrentUser(UserInfo user) {
        USER_THREAD_LOCAL.set(user);
    }

    public static UserInfo getCurrentUser() {
        return (UserInfo)USER_THREAD_LOCAL.get();
    }
}
