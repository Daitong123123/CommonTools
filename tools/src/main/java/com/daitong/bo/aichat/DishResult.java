package com.daitong.bo.aichat;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DishResult {
    private String dishName = "";
    private String complex = "";
    private String dishStep = "";
    private String dishEffect = "";
    private String dishIngredients = "";
    private String dishCost = "";
}
