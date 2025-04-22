package com.daitong.repository.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@TableName("model_config")
public class ModelConfig {

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    private String modelType;

    private String model;

    private boolean selected;

    private String modelName;

    private Date createdAt;
    private Date updatedAt;
}
