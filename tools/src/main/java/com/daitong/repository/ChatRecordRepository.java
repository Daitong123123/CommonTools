package com.daitong.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daitong.bo.message.SendMessageRequest;
import com.daitong.repository.entity.ChatRecord;
import com.daitong.repository.mapper.ChatRecordMapper;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class ChatRecordRepository extends ServiceImpl<ChatRecordMapper, ChatRecord> {

    public void insertRecord(SendMessageRequest request) {
        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setMessage(request.getMessageContent());
        chatRecord.setDeleted(false);
        chatRecord.setRead(false);
        chatRecord.setCreatedAt(new Date());
        chatRecord.setUpdatedAt(new Date());
        chatRecord.setUserIdFrom(request.getUserIdFrom());
        chatRecord.setUserIdTo(request.getUserIdTo());
        chatRecord.setMessageType("text");
        this.save(chatRecord);
    }


}
