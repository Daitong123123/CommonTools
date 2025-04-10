package com.daitong.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class QwenChatService {

    private String apiKey = "sk-61c195da368d4970b92c7e08728a72a5";

    private String systemPromote = "You are a helpful assistant.";

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
                .apiKey(apiKey)
                // 此处以qwen-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model("qwen-plus")
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        return JsonUtils.toJson(gen.call(param)) ;
    }

    public String chat(String content) throws NoApiKeyException, InputRequiredException {
       return chat(content, null);
    }
}
