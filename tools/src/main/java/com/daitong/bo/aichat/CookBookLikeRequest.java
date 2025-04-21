package com.daitong.bo.aichat;

import com.daitong.repository.entity.CookBookLikes;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CookBookLikeRequest {

    private CookBookLikes cookBook;
}
