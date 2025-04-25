package com.daitong.bo.aichat;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DishRequest {
    private String dishType ;
    private Integer dishNumber ;
    private String dishTaste ;
    private Integer complexStart;
    private Integer complexEnd;
    private String preference;
}
