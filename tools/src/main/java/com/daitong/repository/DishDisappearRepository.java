package com.daitong.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daitong.manager.UserManager;
import com.daitong.repository.entity.DishDetail;
import com.daitong.repository.entity.DishDisappear;
import com.daitong.repository.mapper.DishDisappearMapper;
import com.daitong.repository.mapper.DishMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DishDisappearRepository extends ServiceImpl<DishDisappearMapper, DishDisappear> {

    public List<DishDisappear> getUnlikes(){
        QueryWrapper<DishDisappear> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DishDisappear::getUserId, UserManager.getCurrentUser().getUserId());
        return list(queryWrapper);
    }
}
