package com.daitong.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@TableName("user")
public class UserEntity {


    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    private String userId;

    private String userName;

    private String passWord;

    private String coupleId;

    private String phoneNumber;

    private String iconId;

    private Date createdAt;
}
