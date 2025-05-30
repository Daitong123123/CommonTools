package com.daitong.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daitong.manager.IdManager;
import com.daitong.repository.entity.UserEntity;
import com.daitong.repository.mapper.UserMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class UserRepository extends ServiceImpl<UserMapper, UserEntity> {

    public boolean checkUser(String userName, String passWord) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserEntity::getUserName, userName)
                .eq(UserEntity::getPassWord, passWord);
        return CollectionUtils.isNotEmpty(list(queryWrapper));
    }

    public UserEntity getUserInfo(String userName, String passWord) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserEntity::getUserName, userName)
                .eq(UserEntity::getPassWord, passWord);
        return list(queryWrapper).get(0);
    }

    public UserEntity getCoupleInfo(String coupleId, String userId) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserEntity::getCoupleId, coupleId)
                .ne(UserEntity::getUserId, userId);
        return list(queryWrapper).get(0);
    }

    public boolean isUsernameExists(String userName) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserEntity::getUserName, userName);
        return CollectionUtils.isNotEmpty(list(queryWrapper));
    }

    public boolean registerUser(String userName, String passWord) {
        UserEntity entity = new UserEntity();
        entity.setUserName(userName);
        entity.setPassWord(passWord);
        entity.setUserId(String.valueOf(IdManager.getId()));
        entity.setCreatedAt(new Date());
        return this.save(entity);
    }

    public UserEntity getUserInfo(String userId) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserEntity::getUserId, userId);
        return this.getOne(queryWrapper);
    }

    public boolean updateIconId(String userId, String iconId) {
        UpdateWrapper<UserEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .eq(UserEntity::getUserId, userId)
                .set(UserEntity::getIconId, iconId);
        return update(updateWrapper);
    }
}
