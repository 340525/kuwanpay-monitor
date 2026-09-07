package com.kuwanpay.monitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP 请求工具类
 */
public class HttpHelper {
    
    private Map<String, String> params;
    private int timeout = 10000; // 10 秒超时
    
    public HttpHelper() {
        this.params = new HashMap<>();
    }
    
    /**
     * 添加参数
     */
    public void addParam(String key, String value) {
        params.put(key, value);
    }
    
    /**
     * 发送 POST 请求
     */
    public String post(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        
        // 构建请求体
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        
        // 发送请求
        OutputStream os = conn.getOutputStream();
        os.write(sb.toString().getBytes("UTF-8"));
        os.flush();
        os.close();
        
        // 读取响应
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            return response.toString();
        } else {
            throw new Exception("HTTP 请求失败，响应码：" + responseCode);
        }
    }
    
    /**
     * 发送 GET 请求
     */
    public String get(String urlStr) throws Exception {
        // 构建 URL
        StringBuilder sb = new StringBuilder(urlStr);
        if (urlStr.contains("?")) {
            sb.append("&");
        } else {
            sb.append("?");
        }
        
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) {
                sb.append("&");
            }
            sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            first = false;
        }
        
        URL url = new URL(sb.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);
        
        // 读取响应
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            return response.toString();
        } else {
            throw new Exception("HTTP 请求失败，响应码：" + responseCode);
        }
    }
}
