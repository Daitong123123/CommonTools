package com.daitong.bo.message;

import com.daitong.bo.common.PageRequest;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetMessageRequest extends PageRequest {

    private String userIdFrom;

    private String userIdTo;
}
