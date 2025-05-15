package com.daitong.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daitong.repository.entity.FileManager;
import com.daitong.repository.mapper.FileManagerMapper;
import org.springframework.stereotype.Repository;

@Repository
public class FileManagerRepository extends ServiceImpl<FileManagerMapper, FileManager> {

    public FileManager getByHash(String hash) {
        return getOne(new LambdaQueryWrapper<FileManager>()
                .eq(FileManager::getContentHash, hash));
    }

    public FileManager getByFileId(String fileId) {
        return getOne(new LambdaQueryWrapper<FileManager>()
                .eq(FileManager::getFileId, fileId));
    }
}
