package com.daitong.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@TableName("couple_account_record")
public class CoupleAccountRecord {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String userId;          // 记录创建者ID
    private String coupleId;        // 情侣关系ID，用于关联两个用户
    private String type;            // 记录类型：expense/income
    private String amount;          // 金额
    private String category;        // 分类
    private String note;            // 备注
    private Date date;              // 消费日期
    private Date createdAt;         // 创建时间
    private Date updatedAt;         // 更新时间
}
