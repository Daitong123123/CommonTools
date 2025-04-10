package com.daitong.constants;

public interface Promotes {

     String DISH_RECOMMEND_SYS = "你是一个有着丰富经验的厨师,请根据要求推荐菜品";

     String DISH_RECOMMEND_USER = "今天%s不知道吃什么,帮我推荐%s道%s,需要给出详细做法,调料精确到克,火候时长精确到分钟。另外还有四点要求：1.要求做法不能太过复杂。2.要求食材要比较常见。3.味道可以偏辣，不要推荐很甜的（甜点除外）。4.不要说任何多余的话，按照如下json格式返回结果即可，%s。其中dishName表示这道菜的名字。complex表示菜的复杂度，根据你的理解给出1-5的复杂度即可，5代表最复杂，1代表最简单。dishStep表示这道菜制作的详细步骤，要求详细步骤的每一步个步骤后要加一个换行";

}
