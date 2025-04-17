package com.daitong.bo.message;

import com.daitong.bo.common.CommonResponse;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class FriendToBeResponse extends CommonResponse {

    List<FriendToBeInfo> friendToBeRequestList;

}
