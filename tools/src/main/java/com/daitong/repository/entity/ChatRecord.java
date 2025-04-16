package com.daitong.repository.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@TableName("chat_record")
public class ChatRecord {

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    private String userIdFrom;

    private String userIdTo;

    private String message;

    private String messageType;

    private boolean isRead ;

    private boolean isDeleted ;

    private Date createdAt;
    private Date updatedAt;
}
