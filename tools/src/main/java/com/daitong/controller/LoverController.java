package com.daitong.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.daitong.bo.common.CommonResponse;
import com.daitong.bo.message.FriendShipRequest;
import com.daitong.bo.message.FriendShipResponse;
import com.daitong.bo.message.FriendToBeInfo;
import com.daitong.bo.message.FriendToBeRequest;
import com.daitong.bo.message.FriendToBeResponse;
import com.daitong.repository.LoverShipRepository;
import com.daitong.repository.entity.FriendShip;
import com.daitong.repository.entity.LoverShip;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Log4j2
public class LoverController {

    @Autowired
    private LoverShipRepository loverShipRepository;

    @PostMapping("/lover-request")
    public CommonResponse loverRequest(@RequestBody FriendToBeRequest friendToBeRequest) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            commonResponse.setCode("200");
            commonResponse.setMessage("请求成功");
            LoverShip loverShip = new LoverShip();
            loverShip.setUserId(friendToBeRequest.getUserId());
            loverShip.setLoversId(friendToBeRequest.getFriendId());
            loverShip.setWhoRequest(friendToBeRequest.getUserId());
            loverShip.setStatus(0);
            loverShip.setUpdatedAt(new Date());
            loverShip.setCreatedAt(new Date());
            loverShipRepository.save(loverShip);
            return commonResponse;
        } catch (Exception e) {
            log.error("请求失败", e);
            commonResponse.setCode("500");
            commonResponse.setMessage(e.getMessage());
        }
        return commonResponse;
    }

    @PostMapping("/lover-request-agree")
    public CommonResponse loverRequestAgree(@RequestBody FriendToBeRequest friendToBeRequest) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            commonResponse.setCode("200");
            commonResponse.setMessage("请求成功");
            LoverShip oldShip = loverShipRepository.getOne(new QueryWrapper<LoverShip>().lambda().eq(LoverShip::getLoversId, friendToBeRequest.getUserId()).eq(LoverShip::getUserId, friendToBeRequest.getFriendId()));
            oldShip.setStatus(1);
            loverShipRepository.updateById(oldShip);
            LoverShip ship = new LoverShip();
            ship.setStatus(1);
            ship.setUserId(oldShip.getLoversId());
            ship.setWhoRequest(oldShip.getWhoRequest());
            ship.setLoversId(oldShip.getUserId());
            ship.setCreatedAt(new Date());
            ship.setUpdatedAt(new Date());
            loverShipRepository.save(ship);
            return commonResponse;
        } catch (Exception e) {
            log.error("请求失败", e);
            commonResponse.setCode("500");
            commonResponse.setMessage(e.getMessage());
        }
        return commonResponse;
    }

    @PostMapping("/lover-request-disagree")
    public CommonResponse loverRequestDisagree(@RequestBody FriendToBeRequest friendToBeRequest) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            commonResponse.setCode("200");
            commonResponse.setMessage("请求成功");
            LoverShip oldShip = loverShipRepository.getOne(new QueryWrapper<LoverShip>().lambda().eq(LoverShip::getLoversId, friendToBeRequest.getUserId()).eq(LoverShip::getUserId, friendToBeRequest.getFriendId()));
            oldShip.setStatus(2);
            loverShipRepository.updateById(oldShip);
            return commonResponse;
        } catch (Exception e) {
            log.error("请求失败", e);
            commonResponse.setCode("500");
            commonResponse.setMessage(e.getMessage());
        }
        return commonResponse;
    }

    @PostMapping("/lover-request-query")
    public FriendToBeResponse loverRequestQuery(@RequestBody FriendToBeRequest friendToBeRequest) {
        FriendToBeResponse friendToBeResponse = new FriendToBeResponse();
        try {
            friendToBeResponse.setCode("200");
            friendToBeResponse.setMessage("请求成功");
            QueryWrapper<LoverShip> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(LoverShip::getStatus, 0)
                    .and(wrapper -> wrapper
                            .eq(LoverShip::getLoversId, friendToBeRequest.getUserId())
                            .or()
                            .eq(LoverShip::getUserId, friendToBeRequest.getUserId())
                    );
            List<LoverShip> list = loverShipRepository.list(queryWrapper);
            List<FriendToBeInfo> friendToBeInfos = list.stream().map(ship -> {
                FriendToBeInfo friendToBeInfo = new FriendToBeInfo();
                friendToBeInfo.setRequestFrom(ship.getUserId());
                friendToBeInfo.setRequestTo(ship.getLoversId());
                friendToBeInfo.setStatus(String.valueOf(ship.getStatus()));
                return friendToBeInfo;
            }).collect(Collectors.toList());
            friendToBeResponse.setFriendToBeRequestList(friendToBeInfos);
            return friendToBeResponse;
        } catch (Exception e) {
            log.error("请求失败", e);
            friendToBeResponse.setCode("500");
            friendToBeResponse.setMessage(e.getMessage());
        }
        return friendToBeResponse;
    }

    @PostMapping("/lover-ship")
    public FriendShipResponse getMyLover(@RequestBody FriendShipRequest friendShipRequest) {
        QueryWrapper<LoverShip> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(LoverShip::getUserId, friendShipRequest.getUserId())
                .eq(LoverShip::getStatus, 1);
        List<LoverShip> ships = loverShipRepository.list(queryWrapper);
        FriendShipResponse friendShipResponse = new FriendShipResponse();
        friendShipResponse.setFriends(ships.stream().map(LoverShip::getLoversId).collect(Collectors.toList()));
        return friendShipResponse;
    }
}
