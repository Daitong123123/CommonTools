package com.daitong.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class PdsSignatureUtils {
    private static final String HMAC_ALGORITHM = "HmacSHA1";
    private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
    private static final String ENCODING = "UTF-8";
    private static final Pattern PATTERN = Pattern.compile("\\s+");

    /**
     * 生成Authorization头
     */
    public static String generateAuthorization(String accessKeyId, String accessKeySecret,
                                               String httpMethod, String accept,
                                               String contentMd5, String contentType,
                                               String date, Map<String, String> headers,
                                               String resourcePath) throws Exception {
        String signature = generateSignature(accessKeySecret, httpMethod, accept, contentMd5,
                contentType, date, headers, resourcePath);
        return "acs " + accessKeyId + ":" + signature;
    }

    /**
     * 生成签名
     */
    public static String generateSignature(String accessKeySecret, String httpMethod,
                                           String accept, String contentMd5,
                                           String contentType, String date,
                                           Map<String, String> headers, String resourcePath) throws Exception {
        // 构建待签名字符串
        String stringToSign = buildStringToSign(httpMethod, accept, contentMd5,
                contentType, date, headers, resourcePath);

        // 使用HMAC-SHA1算法计算签名
        return calculateSignature(accessKeySecret, stringToSign);
    }

    /**
     * 构建待签名字符串
     */
    private static String buildStringToSign(String httpMethod, String accept,
                                            String contentMd5, String contentType,
                                            String date, Map<String, String> headers,
                                            String resourcePath) throws URISyntaxException {
        // 1. 规范化HTTP头
        String canonicalizedHeaders = getCanonicalizedHeaders(headers);

        // 2. 规范化资源路径 - 只取路径部分
        String canonicalizedResource = getCanonicalizedResource(resourcePath);

        // 3. 构建待签名字符串
        StringBuilder stringToSignBuilder = new StringBuilder();
        stringToSignBuilder.append(httpMethod).append("\n")
                .append(accept == null ? "" : accept).append("\n")
                .append(contentMd5 == null ? "" : contentMd5).append("\n")
                .append(contentType == null ? "" : contentType).append("\n")
                .append(date).append("\n")
                .append(canonicalizedHeaders)
                .append(canonicalizedResource);

        return stringToSignBuilder.toString();
    }

    /**
     * 规范化HTTP头
     */
    private static String getCanonicalizedHeaders(Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return "";
        }

        // 筛选并排序以x-acs-为前缀的HTTP头
        TreeMap<String, String> sortedHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey().trim().toLowerCase();
            if (key.startsWith("x-acs-")) {
                sortedHeaders.put(key, entry.getValue().trim());
            }
        }

        // 构建规范化HTTP头字符串
        StringBuilder canonicalizedHeadersBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedHeaders.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // 合并连续空格
            value = PATTERN.matcher(value).replaceAll(" ");
            canonicalizedHeadersBuilder.append(key).append(":").append(value).append("\n");
        }

        return canonicalizedHeadersBuilder.toString();
    }

    /**
     * 规范化资源路径 - 只保留路径部分
     */
    private static String getCanonicalizedResource(String resourcePath) throws URISyntaxException {
        if (resourcePath == null || resourcePath.isEmpty()) {
            return "/";
        }

        // 解析URI获取路径
        URI uri = new URI(resourcePath);
        String path = uri.getPath();
        if (path == null || path.isEmpty()) {
            path = "/";
        }

        // 规范化路径
        path = normalizePath(path);

        // 处理查询参数
        String query = uri.getQuery();
        if (query == null || query.isEmpty()) {
            return path;
        }

        // 解析并排序查询参数
        Map<String, String> queryParams = parseQueryString(query);
        TreeMap<String, String> sortedParams = new TreeMap<>(queryParams);

        // 构建规范化查询字符串
        StringBuilder canonicalizedQueryBuilder = new StringBuilder();
        canonicalizedQueryBuilder.append(path);
        if (!sortedParams.isEmpty()) {
            canonicalizedQueryBuilder.append("?");
            boolean first = true;
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                if (!first) {
                    canonicalizedQueryBuilder.append("&");
                }
                canonicalizedQueryBuilder.append(urlEncode(entry.getKey()))
                        .append("=")
                        .append(urlEncode(entry.getValue()));
                first = false;
            }
        }

        return canonicalizedQueryBuilder.toString();
    }

    /**
     * 规范化路径
     */
    private static String normalizePath(String path) {
        // 处理斜杠
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        // 移除尾部斜杠，除非路径为根路径
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * 解析查询字符串
     */
    private static Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            } else {
                params.put(keyValue[0], "");
            }
        }

        return params;
    }

    /**
     * URL编码
     */
    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, ENCODING)
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (Exception e) {
            throw new RuntimeException("URL编码失败", e);
        }
    }

    /**
     * 计算HMAC-SHA1签名
     */
    private static String calculateSignature(String accessKeySecret, String stringToSign) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(accessKeySecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signData);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("计算签名失败", e);
        }
    }

    /**
     * 计算输入流的MD5并进行Base64编码
     */
    public static String calculateContentMd5(InputStream inputStream) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            byte[] digest = md.digest();
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("计算MD5失败", e);
        }
    }

    /**
     * 计算字符串的MD5并进行Base64编码
     */
    public static String calculateContentMd5(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("计算MD5失败", e);
        }
    }

    /**
     * 获取当前时间的GMT格式字符串
     */
    public static String getCurrentGmtDate() {
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(new Date());
    }

    /**
     * 将输入流转换为字符串
     */
    public static String inputStreamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }
}