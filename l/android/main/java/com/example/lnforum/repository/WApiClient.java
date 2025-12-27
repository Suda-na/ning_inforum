package com.example.lnforum.repository;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 网络请求工具类，用于与后端API通信
 */
public class WApiClient {
    private static final String TAG = "WApiClient";
    private static final String BASE_URL = "http://192.168.159.1:8080"; // 根据实际情况修改

    /**
     * API响应封装类
     */
    public static class ApiResponse {
        public boolean success;
        public int code;
        public String message;
        public Object data;

        public ApiResponse(boolean success, int code, String message, Object data) {
            this.success = success;
            this.code = code;
            this.message = message;
            this.data = data;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public Object getData() {
            return data;
        }
    }

    /**
     * GET请求
     */
    public static ApiResponse get(String path, Map<String, String> params) {
        try {
            StringBuilder urlBuilder = new StringBuilder(BASE_URL + path);
            if (params != null && !params.isEmpty()) {
                urlBuilder.append("?");
                boolean first = true;
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (!first) {
                        urlBuilder.append("&");
                    }
                    urlBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    urlBuilder.append("=");
                    urlBuilder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    first = false;
                }
            }

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                String responseStr = response.toString();
                Log.d(TAG, "GET响应原始数据: " + responseStr);
                
                JSONObject jsonResponse = new JSONObject(responseStr);
                int code = jsonResponse.optInt("code", responseCode);
                String message = jsonResponse.optString("message", "");
                Object data = jsonResponse.opt("data");
                
                Log.d(TAG, "GET响应解析: code=" + code + ", message=" + message + ", data类型=" + (data != null ? data.getClass().getName() : "null"));
                if (data != null) {
                    Log.d(TAG, "GET响应data内容: " + data.toString());
                }

                return new ApiResponse(true, code, message, data);
            } else {
                Log.e(TAG, "GET请求失败: responseCode=" + responseCode);
                return new ApiResponse(false, responseCode, "请求失败", null);
            }
        } catch (Exception e) {
            Log.e(TAG, "GET请求异常: " + path, e);
            return new ApiResponse(false, -1, e.getMessage(), null);
        }
    }

    /**
     * POST请求（表单格式）
     */
    public static ApiResponse post(String path, Map<String, String> params) {
        try {
            URL url = new URL(BASE_URL + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setDoOutput(true);

            if (params != null && !params.isEmpty()) {
                StringBuilder postData = new StringBuilder();
                boolean first = true;
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (!first) {
                        postData.append("&");
                    }
                    postData.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    postData.append("=");
                    postData.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    first = false;
                }

                OutputStream os = conn.getOutputStream();
                os.write(postData.toString().getBytes("UTF-8"));
                os.flush();
                os.close();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                int code = jsonResponse.optInt("code", responseCode);
                String message = jsonResponse.optString("message", "");
                Object data = jsonResponse.opt("data");

                return new ApiResponse(true, code, message, data);
            } else {
                return new ApiResponse(false, responseCode, "请求失败", null);
            }
        } catch (Exception e) {
            Log.e(TAG, "POST请求异常: " + path, e);
            return new ApiResponse(false, -1, e.getMessage(), null);
        }
    }

    /**
     * POST请求（JSON格式）
     */
    public static ApiResponse postJson(String path, JSONObject jsonData) {
        try {
            URL url = new URL(BASE_URL + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            if (jsonData != null) {
                String jsonString = jsonData.toString();
                Log.d(TAG, "POST JSON请求: " + path + ", 数据: " + jsonString);
                OutputStream os = conn.getOutputStream();
                os.write(jsonString.getBytes("UTF-8"));
                os.flush();
                os.close();
            }

            int responseCode = conn.getResponseCode();
            BufferedReader reader;
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String responseStr = response.toString();
            Log.d(TAG, "POST JSON响应: " + responseStr);
            
            JSONObject jsonResponse = new JSONObject(responseStr);
            int code = jsonResponse.optInt("code", responseCode);
            String message = jsonResponse.optString("message", "");
            Object data = jsonResponse.opt("data");
            
            // 兼容SPostController的响应格式（直接返回success和message）
            if (jsonResponse.has("success")) {
                boolean success = jsonResponse.optBoolean("success", false);
                message = jsonResponse.optString("message", message);
                return new ApiResponse(success, success ? 200 : 400, message, data);
            }

            return new ApiResponse(code == 200, code, message, data);
        } catch (Exception e) {
            Log.e(TAG, "POST JSON请求异常: " + path, e);
            return new ApiResponse(false, -1, e.getMessage(), null);
        }
    }
}

