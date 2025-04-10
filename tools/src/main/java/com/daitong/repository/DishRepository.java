package com.daitong.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daitong.repository.entity.DishDetail;
import com.daitong.repository.mapper.DishMapper;
import org.springframework.stereotype.Repository;

@Repository
public class DishRepository  extends ServiceImpl<DishMapper, DishDetail> {
}
