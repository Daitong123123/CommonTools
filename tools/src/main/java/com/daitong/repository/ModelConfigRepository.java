package com.daitong.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daitong.repository.entity.ModelConfig;
import com.daitong.repository.mapper.ModelConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ModelConfigRepository extends ServiceImpl<ModelConfigMapper, ModelConfig> {

    @Autowired
    private ModelConfigMapper mapper;

    public List<Object> selectObjs(String colName) {
        QueryWrapper<ModelConfig> queryWrapper = new QueryWrapper<>();
        // 使用 DISTINCT 关键字去重，查询 dish_from 列
        queryWrapper.select("DISTINCT "+colName);
        // 执行查询，返回不重复的 dish_from 列表
        return mapper.selectObjs(queryWrapper);
    }

}
