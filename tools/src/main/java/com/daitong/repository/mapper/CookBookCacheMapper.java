package com.daitong.repository.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daitong.manager.UserManager;
import com.daitong.repository.entity.CookBookCache;
import com.daitong.repository.entity.CookBookPreference;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface CookBookCacheMapper extends BaseMapper<CookBookCache> {

}
