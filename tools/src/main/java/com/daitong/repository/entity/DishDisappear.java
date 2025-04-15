package com.daitong.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@TableName("dish_disappear")
public class DishDisappear {


    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    private String dishName;

    private Date createdAt;

    private String userId;
}
