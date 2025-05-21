package com.daitong.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.daitong.bo.aichat.QwenResult;
import com.daitong.config.entity.AliyunConfig;
import com.daitong.config.entity.BytedanceConfig;
import com.daitong.repository.DishDisappearRepository;
import com.daitong.repository.ModelConfigRepository;
import com.daitong.repository.entity.DishDisappear;
import com.daitong.repository.entity.ModelConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.List;

@Service
@Log4j2
public class AiChatService {

    @Autowired
    private ModelConfigRepository modelConfigRepository;

    @Autowired
    private AliyunConfig aliyunConfig;

    @Autowired
    private BytedanceConfig bytedanceConfig;

    private String systemPromote = "You are a helpful assistant.";

    //千问百炼平台
    private String qwenUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";


    // 豆包引擎
    private String doubaoUrl = "https://ark.cn-beijing.volces.com/api/v3/chat/completions";

    @Value("${spring.profiles.active:}")
    private String env ;

    public String chat(String content, String systemPromote) throws NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(StringUtils.isBlank(systemPromote)?this.systemPromote:systemPromote)
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(content)
                .build();
        GenerationParam param = GenerationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(aliyunConfig.getQwenApiKey())
                // 此处以qwen-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model("qwen-plus")
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        return JsonUtils.toJson(gen.call(param).getOutput().getChoices().get(0).getMessage().getContent()) ;
    }

    public String chat(String content) throws NoApiKeyException, InputRequiredException {
       return chat(content, null);
    }

    public String chatToQwen(String content, String systemPromote, String model){
        return chatByHttp(content, systemPromote, qwenUrl, model==null?"qwen-plus":model, aliyunConfig.getQwenApiKey());
    }

    public String chatToAiByConfig(String content, String systemPromote){
        ModelConfig config = modelConfigRepository.getOne(new LambdaQueryWrapper<ModelConfig>().eq(ModelConfig::isSelected, true));
        if("ALIYUN".equals(config.getModelType())){
            return chatToQwen(content,systemPromote,config.getModel());
        } else if ("BYTEDANCE".equals(config.getModelType())) {
            return chatToDoubao(content, systemPromote, config.getModel());
        }else {
            return chatToDoubao(content, systemPromote, null);
        }
    }

    public String chatToDoubao(String content, String systemPromote, String model){
        return chatByHttp(content, systemPromote, doubaoUrl, model==null?"doubao-pro-256k-241115":model, bytedanceConfig.getDoubaoApiKey());
    }


    public String chatByHttp(String content, String systemPromote, String url, String model, String apiKey)  {

        HttpRequest httpRequest = HttpRequest.post(url);
        if("dev".equals(env)){
            java.net.Proxy proxy = new java.net.Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.huawei.com", 8080));
            httpRequest.setProxy(proxy);
        }
        httpRequest.header("Authorization" ,"Bearer "+apiKey);
        JSONObject body = new JSONObject();
        JSONArray messages = new JSONArray();
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", content);
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", StringUtils.isBlank(systemPromote)?this.systemPromote:systemPromote);
        messages.add(userMessage);
        messages.add(systemMessage);
        body.put("model", model);
        body.put("messages", messages);
        httpRequest.body(JSONObject.toJSONString(body));
        HttpResponse response = null;
        try {
            response = httpRequest.execute();
        } catch (Exception e) {
            log.error("请求失败", e);
            return "网络请求失败，翻译失败";
        }
        QwenResult qwenResult = JSONObject.parseObject(response.body(), QwenResult.class);
        String result =  qwenResult.getChoices().get(0).getMessage().getContent();
        return turnToJson(result);
    }

    private String turnToJson(String result){
        return result.replace("json","").replace("```","");
    }
}
