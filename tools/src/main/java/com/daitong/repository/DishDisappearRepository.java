package com.daitong.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daitong.repository.entity.DishDetail;
import com.daitong.repository.entity.DishDisappear;
import com.daitong.repository.mapper.DishDisappearMapper;
import com.daitong.repository.mapper.DishMapper;
import org.springframework.stereotype.Repository;

@Repository
public class DishDisappearRepository extends ServiceImpl<DishDisappearMapper, DishDisappear> {
}
