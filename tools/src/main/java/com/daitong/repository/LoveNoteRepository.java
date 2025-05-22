package com.daitong.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daitong.repository.entity.LoveNote;
import com.daitong.repository.mapper.LoveNoteMapper;
import org.springframework.stereotype.Service;

@Service
public class LoveNoteRepository extends ServiceImpl<LoveNoteMapper, LoveNote> {
}