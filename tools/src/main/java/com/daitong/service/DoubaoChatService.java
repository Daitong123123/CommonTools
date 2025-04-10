package com.daitong.service;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.daitong.bo.aichat.DoubaoRequest;
import com.daitong.bo.aichat.QwenResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class DoubaoChatService {

    private String systemPromote = "You are a helpful assistant.";

    private String doubaoUrl = "https://www.doubao.com/samantha/chat/completion";


    public String chatByHttp(String content, String systemPromote) throws Exception {
        HttpRequest httpRequest = HttpRequest.post(buildUrl());
        java.net.Proxy proxy = new java.net.Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.huawei.com", 8080));
        httpRequest.setProxy(proxy);
        setHead(httpRequest);
        DoubaoRequest doubaoRequest =new DoubaoRequest();
        List<DoubaoRequest.DoubaoMessage> doubaoMessageList = new ArrayList<>();
        DoubaoRequest.DoubaoMessage message = new DoubaoRequest.DoubaoMessage();
        JSONObject text = new JSONObject();
        text.put("text", systemPromote+","+content);
        message.setContent(JSONObject.toJSONString(text));
        message.setContentType(2001);
        doubaoMessageList.add(message);
        DoubaoRequest.CompletionOption completionOption =new DoubaoRequest.CompletionOption();
        doubaoRequest.setMessages(doubaoMessageList);
        doubaoRequest.setCompletionOption(completionOption);

        doubaoRequest.setConversationId("");
        doubaoRequest.setSectionId("");
        doubaoRequest.setLocalMessageId("");
        httpRequest.body(JSONObject.toJSONString(doubaoRequest));
        HttpResponse response = null;
        try {
            response = httpRequest.execute();
        } catch (Exception e) {
            log.error("请求失败", e);
            return "网络请求失败，翻译失败";
        }

        return response.body();
    }

    public String buildUrl(){
        return UrlBuilder.of(doubaoUrl)
                .addQuery("language","zh")
                .addQuery("device_platform","web")
                .addQuery("pc_version","2.13.2")
                .addQuery("pkg_type","release_version")
                .addQuery("region","CN")
                .addQuery("sys_region","CN")
                .addQuery("samantha_web","1")
                .addQuery("version_code","20800")
                .addQuery("aid","497858")
                .addQuery("real_aid","497858")
                .addQuery("web_id","7400679325199124008")
                .addQuery("tea_uuid","7400679325199124008")
                .addQuery("use-olympus-account","1").build();
    }

    public void setHead(HttpRequest httpRequest){
        httpRequest.header("Accept-Language","zh-CN,zh;q=0.9")
                .header("Agw-Js-Conv","str")
                .header("Connection","keep-alive")
                .header("Origin","https://www.doubao.com")
                .header("Sec-Fetch-Dest","empty")
                .header("Sec-Fetch-Mode","cors")
                .header("Sec-Fetch-Site","same-origin")
                .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36")
                .header("content-type","application/json");

    }

}
