package com.daitong.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@TableName("file_manager")
public class FileManager {

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    private String contentHash;

    private String fileId;

    private String fileSize;

    private String fileType;


    private Date createdAt;

    private Date updatedAt;
}
