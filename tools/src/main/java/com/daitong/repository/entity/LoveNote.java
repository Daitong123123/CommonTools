package com.daitong.repository.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@TableName("love_notes")
public class LoveNote {

    @TableId
    private String id;

    private String coupleId;

    private String title;

    private String content;

    private String authorId;

    private String tags;

    private Date createTime;

    private Date updateTime;
}
