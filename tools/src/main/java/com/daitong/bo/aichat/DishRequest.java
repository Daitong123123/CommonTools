package com.daitong.bo.aichat;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DishRequest {
    private String dishType ;
    private String dishNumber ;
    private String dishTaste ;
}
