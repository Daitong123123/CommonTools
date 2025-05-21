package com.daitong.manager;

import com.daitong.bo.common.UserInfo;
import com.daitong.repository.entity.UserEntity;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SessionManager
 *
 * @since 2025-04-11
 */
public class SessionManager {
    private static final ConcurrentHashMap<String, Long> sessions = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, UserInfo> users = new ConcurrentHashMap<>();

    // 添加一个新的会话
    /**
     * addSession
     *
     * @param sessionId sessionId
     */
    public static void addSession(String sessionId, UserEntity user) {
        sessions.put(sessionId, System.currentTimeMillis());
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setCoupleId(user.getCoupleId());
        userInfo.setPhoneNumber(user.getPhoneNumber());
        userInfo.setUserName(user.getUserName());
        users.put(sessionId,userInfo);
    }

    public static UserInfo getUserBySessionId(String sessionId){
        return users.get(sessionId);
    }



    // 验证会话是否有效，并清除过期的会话
    /**
     * isValidSession
     *
     * @param sessionId sessionId
     * @return boolean
     */
    public static boolean isValidSession(String sessionId) {
        if (sessions.containsKey(sessionId)) {
            long creationTime = sessions.get(sessionId);
            long currentTime = System.currentTimeMillis();
            if ((currentTime - creationTime) > 3600 * 1000) {
                // 如果会话已过期，从会话管理器中移除
                sessions.remove(sessionId);
                users.remove(sessionId);
                return false;
            }
            return true;
        }
        return false;
    }

    // 移除会话
    public static void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    // 生成新的 sessionId
    /**
     * generateSessionId
     *
     * @return String
     */
    public static String generateSessionId() {
        return UUID.randomUUID().toString();
    }
}