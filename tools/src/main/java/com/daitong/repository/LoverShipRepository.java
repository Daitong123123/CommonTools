package com.daitong.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daitong.repository.entity.LoverShip;
import com.daitong.repository.mapper.LoverMapper;
import org.springframework.stereotype.Repository;

@Repository
public class LoverShipRepository extends ServiceImpl<LoverMapper, LoverShip> {
}
