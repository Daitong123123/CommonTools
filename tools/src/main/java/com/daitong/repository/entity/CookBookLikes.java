package com.daitong.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@TableName("cook_book_likes")
public class CookBookLikes {
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    private String dishFrom;

    private String userId;

    private String complex;

    private String tasty;

    private String dishName;

    private String dishStep;

    private String dishEffect;

    private Date createdAt;

    private Date updatedAt;

}

