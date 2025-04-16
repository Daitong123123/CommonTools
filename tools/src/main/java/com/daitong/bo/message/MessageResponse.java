package com.daitong.bo.message;

import com.daitong.bo.common.PageResponse;
import com.daitong.repository.entity.ChatRecord;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class MessageResponse extends PageResponse {

    private List<ChatRecord> records;
}
