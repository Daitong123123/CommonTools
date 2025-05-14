package com.daitong.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;

@Service
@Log4j2
public class BaiduTranslateService {

    private String translateUrl = "https://fanyi.baidu.com/ait/text/translate";

    private String Sec_Fetch_Mode = "Sec-Fetch-Mode";
    private String Sec_Fetch_Mode_Value = "cors";

    private String Sec_Fetch_Site = "Sec-Fetch-Site";
    private String Sec_Fetch_Site_Value = "same-origin";

    @Value("${spring.profiles.active:}")
    private String env ;
    public String translateByBaiduMachine(String content, String from, String to) {
        HttpRequest httpRequest = HttpRequest.post(translateUrl);
        if("dev".equals(env)){
            java.net.Proxy proxy = new java.net.Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.huawei.com", 8080));
            httpRequest.setProxy(proxy);
        }
        httpRequest.header(Sec_Fetch_Mode, Sec_Fetch_Mode_Value)
                .header(Sec_Fetch_Site, Sec_Fetch_Site_Value);
        JSONObject body = new JSONObject();
        body.put("query", content);
        body.put("from", from);
        body.put("to", to);
        httpRequest.body(JSONObject.toJSONString(body));
        HttpResponse response = null;
        try {
            response = httpRequest.execute();
        } catch (Exception e) {
            log.error("请求失败", e);
            return "网络请求失败，翻译失败";
        }
        StringBuilder translationResult = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.bodyStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("event: message")) {
                    line = reader.readLine(); // 读取 data 行
                    if (line != null && line.startsWith("data: ")) {
                        String jsonData = line.substring("data: ".length());
                        JSONObject dataObj = JSONObject.parseObject(jsonData);
                        JSONObject innerData = dataObj.getJSONObject("data");
                        String event = innerData.getString("event");
                        if ("Translating".equals(event)) {
                            JSONObject listObj = innerData.getJSONArray("list").getJSONObject(0);
                            String dst = listObj.getString("dst");
                            translationResult.append(dst);
                        }
                    }
                }
            }
            return translationResult.toString();
        } catch (Exception e) {
            log.error("请求失败", e);
            return "网络请求失败，翻译失败";
        }


    }

}
