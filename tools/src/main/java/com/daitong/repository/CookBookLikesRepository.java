package com.daitong.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daitong.manager.UserManager;
import com.daitong.repository.entity.CookBookLikes;
import com.daitong.repository.mapper.CookBookLikesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CookBookLikesRepository extends ServiceImpl<CookBookLikesMapper, CookBookLikes> {
    @Autowired
    private CookBookLikesMapper cookBookLikesMapper;

    // 可以在需要时调用 cookBookLikesMapper 的方法
    public List<Object> selectObjs(String colName) {
        QueryWrapper<CookBookLikes> queryWrapper = new QueryWrapper<>();
        // 添加 where 条件，筛选出指定用户 ID 的记录
        queryWrapper.eq("user_id", UserManager.getCurrentUser().getUserId());
        // 使用 DISTINCT 关键字去重，查询 dish_from 列
        queryWrapper.select("DISTINCT "+colName);
        // 执行查询，返回不重复的 dish_from 列表
        return cookBookLikesMapper.selectObjs(queryWrapper);
    }
}
