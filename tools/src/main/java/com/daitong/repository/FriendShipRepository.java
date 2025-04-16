package com.daitong.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daitong.repository.entity.FriendShip;
import com.daitong.repository.mapper.FriendShipMapper;
import org.springframework.stereotype.Repository;

@Repository
public class FriendShipRepository extends ServiceImpl<FriendShipMapper, FriendShip> {
}
