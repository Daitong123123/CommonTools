package com.daitong.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@TableName("cook_book_cache")
public class CookBookCache {

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    private String dishFrom;

    private Integer complex;

    private String tasty;

    private String dishName;

    private String dishStep;

    private String dishEffect;

    private String dishIngredients;

    private String dishCost;

    private Date createdAt;

    private Date updatedAt;
}
