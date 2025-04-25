package com.daitong.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@TableName("cook_book_preference")
public class CookBookPreference {
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    private String dishId;

    private String userId;

    private Boolean isLike;

    private Date createdAt;

    private Date updatedAt;

}

