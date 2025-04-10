package com.daitong.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;


import java.util.Date;

@Data
@ToString
@TableName("dish_history")
public class DishDetail {


    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    private String dishNumber;

    private String dishName;

    private String dishType;

    private boolean isComplete ;

    private Date createdAt;
}
