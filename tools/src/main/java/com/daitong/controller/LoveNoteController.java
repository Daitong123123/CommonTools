package com.daitong.controller;

import com.daitong.bo.common.BaseResponse;
import com.daitong.repository.LoveNoteRepository;
import com.daitong.repository.entity.LoveNote;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/love-note")
public class LoveNoteController {

    @Resource
    private LoveNoteRepository loveNoteRepository;

    @GetMapping("/list")
    public BaseResponse listNotes(String coupleId) {
        BaseResponse response = new BaseResponse();
        try {
            List<LoveNote> notes = loveNoteRepository.lambdaQuery()
                    .eq(LoveNote::getCoupleId, coupleId)
                    .orderByDesc(LoveNote::getCreateTime)
                    .list();
            response.setData(notes);
            response.setCode("200");
            response.setMessage("查询成功");
        } catch (Exception e) {
            response.setCode("500");
            response.setMessage("查询失败: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/add")
    public BaseResponse addNote(@RequestBody LoveNote note) {
        BaseResponse response = new BaseResponse();
        try {
            note.setId(UUID.randomUUID().toString());
            note.setCreateTime(new Date());
            boolean success = loveNoteRepository.save(note);
            if (success) {
                response.setData(note.getId());
                response.setCode("200");
                response.setMessage("添加成功");
            } else {
                response.setCode("500");
                response.setMessage("添加失败");
            }
        } catch (Exception e) {
            response.setCode("500");
            response.setMessage("添加失败: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/update")
    public BaseResponse updateNote(@RequestBody LoveNote note) {
        BaseResponse response = new BaseResponse();
        try {
            note.setUpdateTime(new Date());
            boolean success = loveNoteRepository.updateById(note);
            response.setData(success);
            response.setCode("200");
            response.setMessage("更新成功");
        } catch (Exception e) {
            response.setCode("500");
            response.setMessage("更新失败: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/delete")
    public BaseResponse deleteNote(String id) {
        BaseResponse response = new BaseResponse();
        try {
            boolean success = loveNoteRepository.removeById(id);
            response.setData(success);
            response.setCode("200");
            response.setMessage("删除成功");
        } catch (Exception e) {
            response.setCode("500");
            response.setMessage("删除失败: " + e.getMessage());
        }
        return response;
    }
}