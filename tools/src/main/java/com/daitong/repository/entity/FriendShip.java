package com.daitong.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@TableName("friendships")
public class FriendShip {

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    private String userId;

    private String friendId;

    // '关系状态（0-待确认，1-已通过，2-已拉黑）
    private int status;

    private Date createdAt;
    private Date updatedAt;
}
